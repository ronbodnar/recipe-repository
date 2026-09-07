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
import { errorInterceptorProviders } from '@core/errors/error.interceptor';
import { routes } from './app.routes';
import { GlobalErrorHandler } from '@core/errors/error.handler';
import { AppTitleStrategy } from '@core/services/app-title.strategy';
import { provideNmfConfigFactory, ValidationMessages } from '@ng-modular-forms/core';
import { provideNmfMaterialConfig } from '@ng-modular-forms/material';
import { ThemeService } from '@core/services/theme.service';
import { provideKeycloakAngular } from './keycloak.config';
import { includeBearerTokenInterceptor } from 'keycloak-angular';
import { provideTranslateService, TranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

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

    provideTranslateService({
      loader: provideTranslateHttpLoader(),
      lang: (
        (navigator.languages && navigator.languages[0]) ||
        navigator.language ||
        'en-US'
      ).replace('-', '_'),
      fallbackLang: 'en_US',
    }),

    provideNmfConfigFactory(() => {
      const translate = inject(TranslateService);

      return {
        translate: (k, p) => translate.instant(k, p),
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
      appearance: 'outline',
      floatLabel: 'always',
    }),

    provideAppInitializer(async () => {
      const themeService = inject(ThemeService);

      themeService.loadTheme();
    }),
  ],
};
