import { Routes } from '@angular/router';
import { UserListComponent } from './users/user-list/user-list.component';
import { LoginComponent } from './users/login/login.component';
import { CreateAccountComponent } from './users/register/register.component';

export const routes: Routes = [
  {
    path: '',
    component: UserListComponent,
  },
  {
    path: 'users/login',
    component: LoginComponent,
  },
  {
    path: 'users/register',
    component: CreateAccountComponent,
  },
];
