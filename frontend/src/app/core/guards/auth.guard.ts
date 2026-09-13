import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import { AuthService } from '@features/auth/auth.service';
import { logDebug } from '@shared/utils/logging';

export const authGuard: CanActivateFn = (route, _state) => {
  const router = inject(Router);
  const snackBar = inject(SnackbarService);
  const authService = inject(AuthService);

  if (!authService.isAuthenticated() || !authService.authenticatedRoles()) {
    return redirectWithSnackbar(router, snackBar);
  }

  if (route.data['requiredRole'] == null) {
    return true;
  }

  const hasRequiredRole = authService
    .authenticatedRoles()
    .some((r) => r.toLowerCase() === route.data['requiredRole']?.toLowerCase());

  if (!hasRequiredRole) {
    logDebug('user does not have the required role. user roles:', authService.authenticatedRoles());
    return redirectWithSnackbar(router, snackBar);
  }

  return true;
};

function redirectWithSnackbar(router: Router, snackBar: SnackbarService) {
  setTimeout(() => snackBar.openSnackBar(SnackbarType.ERROR, 'errors.notAuthorized'));
  return router.createUrlTree(['/']);
}
