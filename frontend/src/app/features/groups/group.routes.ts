import { Route } from '@angular/router';

export const routes: Route[] = [
  {
    path: 'list',
    title: 'groups.list.title',
    loadComponent: () =>
      import('./group-list/group-list.component').then((m) => m.GroupListComponent),
  },
  {
    path: 'edit/:id',
    title: 'groups.edit.title',
    loadComponent: () =>
      import('./edit-group/edit-group.component').then((m) => m.EditGroupComponent),
  },
  {
    path: ':id/invite/:inviteCode',
    title: 'groups.invite.title',
    loadComponent: () =>
      import('./group-invite-response/group-invite-response.component').then(
        (m) => m.GroupInviteResponseComponent,
      ),
  },
  {
    path: 'details/:id',
    title: 'groups.details.title',
    loadComponent: () =>
      import('./group-details/group-details.component').then((m) => m.GroupDetailsComponent),
  },
];
