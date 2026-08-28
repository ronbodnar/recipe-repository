import { Router, CanActivateFn } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { inject } from '@angular/core';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';

export const roleGuard: CanActivateFn = (route, _state) => {
  const router = inject(Router);
  const snackBar = inject(SnackbarService);
  const authService = inject(AuthenticationService);

  const user = authService.authUser();

  if (user == null) {
    return redirectWithSnackbar(router, snackBar);
  }

  if (!authService.isAuthenticated()) {
    //} || !user.authorities) {
    return redirectWithSnackbar(router, snackBar);
  }

  if (route.data['requiredRole'] == null) {
    return true;
  }

  const hasRequiredRole = false; /*user.authorities.some(
    (r) =>
      r['authority'] === 'ADMINISTRATOR' ||
      r['authority'].toLowerCase() === route.data['requiredRole']?.toLowerCase(),
  );*/

  if (!hasRequiredRole) {
    return redirectWithSnackbar(router, snackBar);
  }

  return true;
};

function redirectWithSnackbar(router: Router, snackBar: SnackbarService) {
  setTimeout(() => snackBar.openSnackBar(SnackbarType.ERROR, 'errors.notAuthorized'));
  return router.createUrlTree(['/']);
}
