import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import { AuthenticationService } from '@core/services/authentication.service';

export const authGuard: CanActivateFn = (route, _state) => {
  const router = inject(Router);
  const snackBar = inject(SnackbarService);
  const authService = inject(AuthenticationService);

  console.log('is authenticated? ', authService.isAuthenticated());
  console.log('authenticated roles: ', authService.authenticatedRoles());

  if (!authService.isAuthenticated() || !authService.authenticatedRoles()) {
    console.log('user is not authenticated or has no roles');
    return redirectWithSnackbar(router, snackBar);
  }

  if (route.data['requiredRole'] == null) {
    console.log('no required role specified');
    return true;
  }

  const hasRequiredRole = authService
    .authenticatedRoles()
    .some((r) => r.toLowerCase() === route.data['requiredRole']?.toLowerCase());

  if (!hasRequiredRole) {
    console.log(
      'user does not have the required role. user roles:',
      authService.authenticatedRoles(),
    );
    return redirectWithSnackbar(router, snackBar);
  }

  return true;
};

function redirectWithSnackbar(router: Router, snackBar: SnackbarService) {
  setTimeout(() => snackBar.openSnackBar(SnackbarType.ERROR, 'errors.notAuthorized'));
  return router.createUrlTree(['/']);
}
