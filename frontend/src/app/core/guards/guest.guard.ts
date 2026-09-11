import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthenticationService } from '@core/services/authentication.service';

export const guestGuard: CanActivateFn = () => {
  const router = inject(Router);
  const authService = inject(AuthenticationService);

  if (authService.isAuthenticated()) {
    return router.createUrlTree(['/app/recipes/list']);
  }

  return true;
};
