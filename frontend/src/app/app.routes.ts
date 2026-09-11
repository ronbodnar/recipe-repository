import { Routes } from '@angular/router';
import { LandingComponent } from './features/landing/landing.component';
import { guestGuard } from '@core/guards/guest.guard';
import { authGuard } from '@core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
    canActivate: [guestGuard],
  },
  {
    path: 'app/auth',
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.routes),
  },
  {
    path: 'app/groups',
    canActivate: [authGuard],
    loadChildren: () => import('./features/groups/group.routes').then((m) => m.routes),
  },
  {
    path: 'app/users',
    canActivate: [authGuard],
    loadChildren: () => import('./features/users/user.routes').then((m) => m.routes),
  },
  {
    path: 'app/recipes',
    canActivate: [authGuard],
    loadChildren: () => import('./features/recipes/recipe.routes').then((m) => m.routes),
  },
  {
    path: 'app/settings',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/settings/settings.component').then((m) => m.SettingsComponent),
    loadChildren: () => import('./features/settings/settings.routes').then((m) => m.routes),
  },
];
