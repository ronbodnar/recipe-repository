import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import Keycloak from 'keycloak-js';

export const landingGuard: CanActivateFn = () => {
  const router = inject(Router);

  const keycloak = inject(Keycloak);

  if (keycloak.authenticated) {
    return router.createUrlTree(['/app/recipes/list']);
  }

  return true;
};
