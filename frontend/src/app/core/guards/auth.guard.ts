import { Router, CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import { AuthenticationService } from '@core/services/authentication.service';

export const authGuard: CanActivateFn = (route, _state) => {
  const router = inject(Router);
  const snackBar = inject(SnackbarService);
  const authService = inject(AuthenticationService);

  if (!authService.isAuthenticated()) {
    return redirectWithSnackbar(router, snackBar);
  }

  if (route.data['requiredRole'] == null) {
    return true;
  }

  const hasRequiredRole = authService
    .authUser()
    ?.roles.some((r) => r.toLowerCase() === route.data['requiredRole']?.toLowerCase());

  if (!hasRequiredRole) {
    return redirectWithSnackbar(router, snackBar);
  }

  return true;
};

function redirectWithSnackbar(router: Router, snackBar: SnackbarService) {
  setTimeout(() => snackBar.openSnackBar(SnackbarType.ERROR, 'errors.notAuthorized'));
  return router.createUrlTree(['/']);
}
