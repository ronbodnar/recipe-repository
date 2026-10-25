import { inject } from '@angular/core';
import { NavItem } from './nav-item.interface';
import { environment } from '@env';
import { AuthService } from '@features/auth/auth.service';
import { DialogService } from '@shared/ui/dialog/dialog.service';
import { UserService } from '@features/users/user.service';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';

export const getNavItems: () => NavItem[] = () => {
  const authService = inject(AuthService);
  const dialogService = inject(DialogService);
  const userService = inject(UserService);
  const snackbarService = inject(SnackbarService);

  // Trigger the computation of the authenticated user signal to ensure it's up-to-date when changing accounts
  authService.authUser();

  console.log('Authenticated User inside getNavItems:', authService.authUser());
  console.log('Authenticated Roles inside getNavItems', authService.authenticatedRoles());

  return [
    {
      sectionName: 'myRecipes',
      icon: 'menu_book',
      label: 'navigation.myRecipes',
      route: '/app/recipes/list',
    },
    {
      sectionName: 'addRecipe',
      icon: 'add',
      label: 'navigation.addRecipe',
      route: '/app/recipes/edit/new',
    },

    {
      sectionName: 'discoverRecipes',
      icon: 'explore',
      label: 'navigation.discoverRecipes',
      route: '/app/recipes/discover',
    },

    ...(environment.enableSelfHosting
      ? []
      : [
          {
            sectionName: 'groupRecipes',
            icon: 'group_work',
            label: 'navigation.groupRecipes',
            route: '/app/recipes/groups',
          },
          {
            sectionName: 'myGroups',
            icon: 'group',
            label: 'navigation.myGroups',
            route: '/app/groups/list',
          },
        ]),

    ...(authService.hasRole('admin')
      ? [
          {
            sectionName: 'inviteUser',
            icon: 'person_add',
            label: 'navigation.inviteUser',
            onClick: () => {
              const dialog = dialogService.openInputDialog(
                'auth.inviteToRegister.title',
                'auth.inviteToRegister.instructions',
                'text',
                {
                  submitButtonLabel: 'auth.inviteToRegister.submitButton',
                },
              );
              dialog.afterClosed().subscribe((response) => {
                if (response) {
                  userService.inviteUserToRegister(response).subscribe({
                    next: () => {
                      snackbarService.openSnackBar(
                        SnackbarType.SUCCESS,
                        'auth.inviteToRegister.successMessage',
                      );
                    },
                    error: () => {
                      snackbarService.openSnackBar(
                        SnackbarType.ERROR,
                        'auth.inviteToRegister.errorMessage',
                      );
                    },
                  });
                }
              });
            },
          },
        ]
      : []),

    {
      sectionName: 'settings',
      icon: 'settings',
      label: 'navigation.settings',
      route: '/app/settings',
    },
    {
      sectionName: 'logout',
      icon: 'logout',
      label: 'navigation.logout',
      onClick: () => authService.logout().subscribe(),
    },
  ];
};
