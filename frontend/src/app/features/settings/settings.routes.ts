import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'general',
    pathMatch: 'full',
  },
  {
    path: 'general',
    title: 'settings.general.title',
    loadComponent: () =>
      import('./general/general.component').then((m) => m.SettingsGeneralComponent),
    //canActivate: [roleGuard],
  },
  {
    path: 'profile',
    title: 'settings.profile.title',
    loadComponent: () =>
      import('./profile/profile.component').then((m) => m.SettingsProfileComponent),
    //canActivate: [roleGuard],
  },
  {
    path: 'security',
    title: 'settings.security.title',
    loadComponent: () =>
      import('./security/security.component').then((m) => m.SettingsSecurityComponent),
    //canActivate: [roleGuard],
  },
];
