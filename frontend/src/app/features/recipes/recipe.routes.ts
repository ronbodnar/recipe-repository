import { ActivatedRouteSnapshot, Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'list',
    title: 'recipes.list.title',
    loadComponent: () =>
      import('./recipe-list/recipe-list.component').then((m) => m.RecipeListComponent),
  },
  {
    path: 'edit/:id',
    title: (route: ActivatedRouteSnapshot) => {
      const recipeId = route.paramMap.get('id');
      return recipeId === 'new' ? 'recipes.edit.title.new' : 'recipes.edit.title.existing';
    },
    loadComponent: () =>
      import('./edit-recipe/edit-recipe.component').then((m) => m.EditRecipeComponent),
  },
  {
    path: 'details/:id',
    loadComponent: () =>
      import('./recipe-details/recipe-details.component').then((m) => m.RecipeDetailsComponent),
  },
];
