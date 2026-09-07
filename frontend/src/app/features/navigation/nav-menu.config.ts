import { AuthenticationService } from '@core/services/authentication.service';
import { NavItem } from './nav-item.interface';
import { inject } from '@angular/core';
import { FeatureFlagService } from '@core/services/feature-flag.service';

export const getNavItems = (): NavItem[] => {
  const authService = inject(AuthenticationService);
  const featureFlags = inject(FeatureFlagService);

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

    ...(featureFlags.isFeatureEnabled('discover')
      ? [
          {
            sectionName: 'discoverRecipes',
            icon: 'explore',
            label: 'navigation.discoverRecipes',
            route: '/app/recipes/discover',
          },
        ]
      : []),

    ...(featureFlags.isFeatureEnabled('groups')
      ? [
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
      onClick: async () => await authService.logout(),
    },
  ];
};
