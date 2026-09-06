import { AuthenticationService } from '@core/services/authentication.service';
import { NavItem } from './nav-item.interface';
import { inject } from '@angular/core';

export const getNavItems = (): NavItem[] => {
  const authService = inject(AuthenticationService);

  return [
    {
      sectionName: 'my-recipes',
      icon: 'menu_book',
      label: 'navigation.myRecipes',
      route: '/app/recipes/list',
    },
    {
      sectionName: 'add-recipe',
      icon: 'add',
      label: 'navigation.addRecipe',
      route: '/app/recipes/edit/new',
    },
    {
      sectionName: 'discover-recipes',
      icon: 'explore',
      label: 'navigation.discoverRecipes',
      route: '/app/recipes/discover',
    },
    {
      sectionName: 'my-groups',
      icon: 'group',
      label: 'navigation.myGroups',
      route: '/app/groups/list',
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
