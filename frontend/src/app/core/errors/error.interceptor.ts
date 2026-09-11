import { Injectable, Injector, inject, signal } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HTTP_INTERCEPTORS,
  HttpErrorResponse,
} from '@angular/common/http';
import {
  catchError,
  finalize,
  Observable,
  ReplaySubject,
  switchMap,
  take,
  tap,
  throwError,
} from 'rxjs';
import { ApiError } from '../models/api-error.model';
import { ErrorService } from './error.service';
import { ErrorCode } from '../interfaces/error-code.interface';
import { AuthenticationService } from '@core/services/authentication.service';

/*
 * This interceptor does the following:
 * - Handles expired access tokens by refreshing the token and retrying the request.
 * - Dispatches the error(s) to the ErrorHandlerService for all other errors.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  private errorHandler = inject(ErrorService);
  private injector = inject(Injector);

  private get authService(): AuthenticationService {
    return this.injector.get(AuthenticationService);
  }

  private refreshInProgress = signal<boolean>(false);

  private refreshTokenQueue = new ReplaySubject<boolean>(1);

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        const apiError = ApiError.fromResponse(err.error);

        const isRefreshRequest = req.url.includes('refresh');
        const accessTokenExpired = apiError.code === ErrorCode.INVALID_ACCESS_TOKEN;

        if (accessTokenExpired && !isRefreshRequest) {
          return this.refreshTokenAndRetryRequest(req, next);
        }

        if (apiError.code === ErrorCode.INVALID_REFRESH_TOKEN) {
          this.authService.clearAuthentication();
        }

        if (!apiError.hasFormError()) {
          this.errorHandler.displayError(apiError);
        }

        return throwError(() => apiError);
      }),
    );
  }

  private refreshTokenAndRetryRequest(req: HttpRequest<unknown>, next: HttpHandler) {
    if (this.refreshInProgress()) {
      return this.refreshTokenQueue.pipe(
        take(1),
        switchMap((success) => {
          if (!success) {
            return throwError(() => new ApiError({ code: ErrorCode.INVALID_REFRESH_TOKEN }));
          }
          return next.handle(req);
        }),
      );
    }

    this.refreshTokenQueue = new ReplaySubject<boolean>(1);
    this.refreshInProgress.set(true);

    return this.authService.refreshToken().pipe(
      switchMap(() => next.handle(req)),
      tap(() => this.refreshTokenQueue.next(true)),
      catchError((err) => {
        // If the error is the REFRESH itself failing
        if (err instanceof HttpErrorResponse && err.url?.includes('refresh')) {
          this.refreshTokenQueue.next(false);
          this.authService.clearAuthentication();
        }

        // If the error is the RETRIED request, throw it so the main interceptor can handle it normally.
        return throwError(() => err);
      }),
      finalize(() => {
        this.refreshInProgress.set(false);
        this.refreshTokenQueue.complete();
      }),
    );
  }
}

export const errorInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
];
