import { Routes } from '@angular/router';
import { LandingComponent } from './features/landing/landing.component';
import { landingGuard } from '@core/guards/landing.guard';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
    canActivate: [landingGuard],
  },
  {
    path: 'app/users',
    loadChildren: () => import('./features/users/users.routes').then((m) => m.routes),
  },
  {
    path: 'app/recipes',
    loadChildren: () => import('./features/recipes/recipe.routes').then((m) => m.routes),
  },
  {
    path: 'app/settings',
    loadComponent: () =>
      import('./features/settings/settings.component').then((m) => m.SettingsComponent),
    //canActivate: [roleGuard],
    loadChildren: () => import('./features/settings/settings.routes').then((m) => m.routes),
  },
];
