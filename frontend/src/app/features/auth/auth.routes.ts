import { Routes } from '@angular/router';
import { UserLoginComponent } from './login/login.component';
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
      import('./register/register.component').then((m) => m.UserRegisterComponent),
    canActivate: [guestGuard],
  },
  {
    path: 'forgot-password',
    title: 'auth.forgotPassword.title',
    loadComponent: () =>
      import('./forgot-password/forgot-password.component').then((m) => m.ForgotPasswordComponent),
    canActivate: [guestGuard],
  },
  {
    path: 'reset-password',
    title: 'auth.resetPassword.title',
    loadComponent: () =>
      import('./reset-password/reset-password.component').then((m) => m.ResetPasswordComponent),
    canActivate: [guestGuard],
  },
];
