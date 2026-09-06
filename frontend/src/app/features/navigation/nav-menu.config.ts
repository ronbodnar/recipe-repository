import { AuthenticationService } from '@core/services/authentication.service';
import { NavItem } from './nav-item.interface';
import { inject } from '@angular/core';

export const getNavItems = (): NavItem[] => {
  const authService = inject(AuthenticationService);

  return [
    {
      sectionName: 'recipes',
      icon: 'restaurant_menu',
      label: 'navigation.recipes',
      children: [
        {
          icon: 'menu_book',
          label: 'navigation.myRecipes',
          route: '/app/recipes/list',
        },
        {
          icon: 'add',
          label: 'navigation.addRecipe',
          route: '/app/recipes/edit/new',
        },
        {
          icon: 'explore',
          label: 'navigation.discoverRecipes',
          route: '/app/recipes/discover',
        },
      ],
    },
    {
      sectionName: 'groups',
      icon: 'groups_3',
      label: 'navigation.groups',
      children: [
        {
          icon: 'group',
          label: 'navigation.myGroups',
          route: '/app/groups/list',
        },
        {
          icon: 'group_add',
          label: 'navigation.addGroup',
          route: '/app/groups/edit/new',
        },
      ],
    },
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
      onClick: async () => await authService.logout(),
    },
  ];
};
