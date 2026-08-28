import {
  ApplicationConfig,
  ErrorHandler,
  inject,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter, TitleStrategy, withViewTransitions } from '@angular/router';
import { provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { httpInterceptorProviders } from './core/interceptors/http.interceptor';
import { routes } from './app.routes';
import { errorInterceptorProviders } from '@core/errors/error.interceptor';
import { GlobalErrorHandler } from '@core/errors/error.handler';
import { AppTitleStrategy } from '@core/services/app-title.strategy';
import { provideNmfConfigFactory, ValidationMessages } from '@ng-modular-forms/core';
import { TranslateService } from '@core/services/translate.service';
import { provideNmfMaterialConfig } from '@ng-modular-forms/material';
import { ThemeService } from '@core/services/theme.service';
import { provideKeycloakAngular } from './keycloak.config';
import { includeBearerTokenInterceptor } from 'keycloak-angular';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptorsFromDi(), withInterceptors([includeBearerTokenInterceptor])),

    httpInterceptorProviders,
    errorInterceptorProviders,

    { provide: TitleStrategy, useClass: AppTitleStrategy },
    { provide: ErrorHandler, useClass: GlobalErrorHandler },

    provideKeycloakAngular(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withViewTransitions()),

    provideNmfConfigFactory(() => {
      const translate = inject(TranslateService);

      return {
        translate: (k, p) => translate.get(k, p),
        translations: {
          validationMessages: {
            required: 'errors.fields.required',
            email: 'errors.fields.email',
            minLength: 'errors.fields.minLength',
            maxLength: 'errors.fields.maxLength',
            pattern: 'errors.fields.pattern',
            min: 'Minimum value is {{min}}',
            max: 'Maximum value is {{max}}',
            fallback: 'Invalid value',
          } as ValidationMessages,
        },
      };
    }),

    provideNmfMaterialConfig({
      detachLabels: true,
    }),

    provideAppInitializer(async () => {
      const translate = inject(TranslateService);
      const themeService = inject(ThemeService);

      await translate.loadLanguage('es');

      themeService.loadTheme();
    }),
  ],
};
