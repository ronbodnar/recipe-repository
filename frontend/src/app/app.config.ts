import { APP_INITIALIZER, ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HttpClient, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { httpInterceptorProviders } from './http.interceptor';
import { provideAnimations } from '@angular/platform-browser/animations';
import { AuthenticationService } from './authentication.service';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    httpInterceptorProviders,
    provideHttpClient(withInterceptorsFromDi()),
    provideRouter(routes),
    provideAnimations(),
    {
      provide: APP_INITIALIZER,
      useFactory: (authenticationService: AuthenticationService) => {
        return () => {
           return authenticationService.checkAuthentication()
        }
      },
      multi: true,
      deps: [HttpClient],
    },
  ]
};
