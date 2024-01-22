import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { UserService } from "./accounts/account.service";
import { provideHttpClient } from "@angular/common/http";

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    UserService,
    provideRouter(routes)
  ]
};
