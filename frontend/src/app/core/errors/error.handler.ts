import { ErrorHandler, Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ErrorService } from './error.service';
import { AuthenticationService } from '../services/authentication.service';
import { environment } from '@env';
import { logError } from '@shared/utils/logging';

export interface FrontendError {
  timestamp: string;
  route: string;
  userId: string;
  userAgent: string;
  message: string;
  stackTrace: string;
}

@Injectable({ providedIn: 'root' })
export class GlobalErrorHandler implements ErrorHandler {
  private authService = inject(AuthenticationService);
  private errorService = inject(ErrorService);
  private router = inject(Router);

  handleError(error: unknown): void {
    const timestamp = new Date().toISOString();
    const route = this.router.url;
    const message = error instanceof Error ? error.message : String(error);
    const stackTrace = error instanceof Error ? (error.stack?.split('\n') ?? []) : [];

    const relevantStack = stackTrace.filter(
      (line) =>
        !line.includes('@angular') && !line.includes('vite') && !line.includes('node_modules'),
    );

    const logPayload = {
      timestamp,
      route,
      userId: this.authService.authUser()?.id,
      userAgent: navigator.userAgent,
      message,
      stackTrace: relevantStack.join('\n'),
    } as FrontendError;

    logError('Handling global error:', error);

    if (environment.production) {
      this.errorService.logFrontendError(logPayload);
    }
  }
}
