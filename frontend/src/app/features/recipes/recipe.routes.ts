import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';
import { environment } from '@env';

export const routes: Routes = [
  {
    path: 'list',
    title: 'recipes.list.myRecipes.title',
    loadComponent: () =>
      import('./recipe-list/recipe-list.component').then((m) => m.RecipeListComponent),
    data: {
      source: 'myRecipes',
    },
  },

  ...(environment.enableDiscover
    ? [
        {
          path: 'discover',
          title: 'recipes.list.discover.title',
          loadComponent: () =>
            import('./recipe-list/recipe-list.component').then((m) => m.RecipeListComponent),
          data: {
            source: 'discover',
          },
        },
      ]
    : []),

  {
    path: 'groups',
    title: 'recipes.list.groupRecipes.title',
    loadComponent: () =>
      import('./recipe-list/recipe-list.component').then((m) => m.RecipeListComponent),
    data: {
      source: 'groupRecipes',
    },
  },

  {
    path: 'edit/:id',
    title: (route: ActivatedRouteSnapshot) => {
      const recipeId = route.paramMap.get('id');
      return recipeId === 'new' ? 'recipes.edit.title.new' : 'recipes.edit.title.existing';
    },
    loadComponent: () =>
      import('./edit-recipe/edit-recipe.component').then((m) => m.EditRecipeComponent),
    data: () => {
      const route = inject(ActivatedRouteSnapshot);
      const recipeId = route.paramMap.get('id');
      return {
        requiredRole: recipeId === 'new' ? 'permission:create-recipe' : 'permission:edit-recipe',
      };
    },
  },

  {
    path: 'details/:id',
    loadComponent: () =>
      import('./recipe-details/recipe-details.component').then((m) => m.RecipeDetailsComponent),
  },
];
