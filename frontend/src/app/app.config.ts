import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { UserService } from "./users/user.service";
import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { httpInterceptorProviders } from './http.interceptor';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    httpInterceptorProviders,
    provideHttpClient(withInterceptorsFromDi()),
    UserService,
    provideRouter(routes),
  ]
};
