import { Routes } from '@angular/router';
import { UserListComponent } from './accounts/account-list/account-list.component';
import { LoginComponent } from './accounts/login/login.component';
import { CreateAccountComponent } from './accounts/create-account/create-account.component';

export const routes: Routes = [
  {
    path: '',
    component: UserListComponent,
  },
  {
    path: 'account/login',
    component: LoginComponent,
  },
  {
    path: 'account/create',
    component: CreateAccountComponent,
  },
];
