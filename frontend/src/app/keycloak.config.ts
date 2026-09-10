import { environment } from '@env';
import {
  provideKeycloak,
  createInterceptorCondition,
  IncludeBearerTokenCondition,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  withAutoRefreshToken,
  AutoRefreshTokenService,
  UserActivityService,
} from 'keycloak-angular';

const apiCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
  urlPattern: environment.production
    ? /^https:\/\/recipes\.ronbodnar\.com\/gateway(\/.*)?$/i
    : /^http:\/\/localhost:8080(\/.*)?$/i,
});

export const provideKeycloakAngular = () =>
  provideKeycloak({
    config: environment.keycloak,
    initOptions: {
      onLoad: 'check-sso',
      checkLoginIframe: false,
      silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
      redirectUri: window.location.origin + '/',
    },
    features: [
      withAutoRefreshToken({
        onInactivityTimeout: 'logout',
      }),
    ],
    providers: [
      AutoRefreshTokenService,
      UserActivityService,
      {
        provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
        useValue: [apiCondition],
      },
    ],
  });
