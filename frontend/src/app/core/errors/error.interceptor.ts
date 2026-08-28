import { Injectable, inject, signal } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HTTP_INTERCEPTORS,
  HttpErrorResponse,
} from '@angular/common/http';
import { catchError, Observable, ReplaySubject, throwError } from 'rxjs';
import { ApiError } from '../models/api-error.model';
import { ErrorService } from './error.service';
import { Router } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { ErrorCode } from '../interfaces/error-code.interface';

/*
 * This interceptor does the following:
 * - Handles expired access tokens by refreshing the token and retrying the request.
 * - Dispatches the error(s) to the ErrorHandlerService for all other errors.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  private router = inject(Router);
  private authService = inject(AuthenticationService);
  private errorHandler = inject(ErrorService);

  private refreshInProgress = signal<boolean>(false);

  private refreshTokenQueue = new ReplaySubject<boolean>(1);

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        const apiError = ApiError.fromResponse(err.error ?? { code: err.status });

        if (apiError.code === ErrorCode.INVALID_REFRESH_TOKEN) {
          //this.authService.clearAuthentication();
          /*           if (this.router.url !== '/') {
            this.router.navigate(['/']);
          } */
        }

        if (!apiError.hasFormError()) {
          this.errorHandler.displayError(apiError);
        }

        return throwError(() => apiError);
      }),
    );
  }
}

export const errorInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
];
