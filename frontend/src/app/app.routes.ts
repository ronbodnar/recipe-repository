import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { LoginComponent } from './features/users/login/login.component';
import { RegisterComponent } from './features/users/register/register.component';
import { AddRecipeComponent } from './features/recipes/add-recipe/add-recipe.component';
import { EditRecipeComponent } from './features/recipes/edit-recipe/edit-recipe.component';
import { ViewRecipeComponent } from './features/recipes/view-recipe/view-recipe.component';

export const routes: Routes = [
  {
    path: '',
    title: 'Recipe Repository',
    component: HomeComponent,
  },
  {
    path: 'users/login',
    title: 'Account Login | Recipe Repository',
    component: LoginComponent,
  },
  {
    path: 'users/register',
    title: 'Registration | Recipe Repository',
    component: RegisterComponent,
  },
  {
    path: 'recipes/add',
    title: 'Add Recipe | Recipe Repository',
    component: AddRecipeComponent,
  },
  {
    path: 'recipes/edit',
    title: 'Edit Recipe | Recipe Repository',
    component: EditRecipeComponent,
  },
  {
    path: 'recipes/view',
    title: 'View Recipe | Recipe Repository',
    component: ViewRecipeComponent,
  },
];
