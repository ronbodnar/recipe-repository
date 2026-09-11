import { Routes } from '@angular/router';
import { UserLoginComponent } from '../auth/login/login.component';
import { guestGuard } from '@core/guards/guest.guard';

export const routes: Routes = [
  {
    path: 'login',
    title: 'auth.login.title',
    component: UserLoginComponent,
    canActivate: [guestGuard],
  },
  {
    path: 'register',
    title: 'auth.register.title',
    loadComponent: () =>
      import('../auth/register/register.component').then((m) => m.UserRegisterComponent),
    canActivate: [guestGuard],
  },
];
