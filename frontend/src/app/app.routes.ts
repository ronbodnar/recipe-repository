import { Routes } from '@angular/router';
import { LandingComponent } from './features/landing/landing.component';
import { landingGuard } from '@core/errors/guards/landing.guard';
import { authGuard } from '@core/errors/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
    canActivate: [landingGuard],
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
