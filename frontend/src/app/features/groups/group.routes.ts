import { Route } from '@angular/router';

export const routes: Route[] = [
  {
    path: 'list',
    title: 'groups.list.my-groups.title',
    loadComponent: () =>
      import('./group-list/group-list.component').then((m) => m.GroupListComponent),
  },
];
