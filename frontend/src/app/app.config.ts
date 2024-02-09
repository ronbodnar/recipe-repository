import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { httpInterceptorProviders } from './http.interceptor';
import { provideAnimations } from '@angular/platform-browser/animations';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    httpInterceptorProviders,
    provideHttpClient(withInterceptorsFromDi()),
    provideRouter(routes),
    provideAnimations()
  ]
};
