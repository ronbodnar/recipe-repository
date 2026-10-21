import { AuthService } from '@features/auth/auth.service';
import { NavItem } from './nav-item.interface';
import { inject } from '@angular/core';
import { environment } from '@env';

export const getNavItems = (): NavItem[] => {
  const authService = inject(AuthService);

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

    ...(environment.enableDiscover
      ? [
          {
            sectionName: 'discoverRecipes',
            icon: 'explore',
            label: 'navigation.discoverRecipes',
            route: '/app/recipes/discover',
          },
        ]
      : []),

    ...(environment.enableGroups
      ? [
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
