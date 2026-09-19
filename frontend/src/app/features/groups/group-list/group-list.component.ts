import { Component, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { PaginatedTableConfig } from '@shared/ui/paginated-table/paginated-table.config';
import { PaginatedTableComponent } from '@shared/ui/paginated-table/paginated-table.component';
import { ButtonComponent } from '@shared/ui/button/button.component';
import {
  DELETE,
  EDIT,
  RowAction,
  TableRowActionsComponent,
} from '@shared/ui/paginated-table/components/table-row-actions/table-row-actions.component';
import { TableLinkComponent } from '@shared/ui/paginated-table/components/table-link/table-link.component';
import { Group } from '../group.types';
import { GroupService } from '../group.service';
import { catchError } from 'rxjs';
import { DialogService } from '@shared/ui/dialog/dialog.service';

@Component({
  selector: 'app-group-list',
  imports: [FluidContainerComponent, PaginatedTableComponent, ButtonComponent],
  templateUrl: './group-list.component.html',
})
export class GroupListComponent {
  private readonly router = inject(Router);
  private readonly groupService = inject(GroupService);
  private readonly dialogService = inject(DialogService);

  tableConfig = computed<PaginatedTableConfig<Group>>(() => {
    return {
      elevation: 4,
      pagination: true,
      filterable: true,
      orderable: true,
      fetchData: (params: Record<string, unknown>) =>
        this.groupService.loadGroups(params).pipe(
          catchError((error) => {
            console.log('Error loading groups:', error);
            throw error;
          }),
        ),
      columns: [
        {
          title: 'Name',
          property: 'name',
          width: '40%',
          angularComponent: TableLinkComponent,
          angularComponentData: (row) => ({
            input: {
              label: row.name,
              routerLink: `/app/groups/details/${row.id}`,
              target: '_self',
            },
          }),
        },
        {
          title: 'Members',
          property: 'memberCount',
          type: 'number',
          filterable: false,
        },
        {
          title: 'Created At',
          property: 'createdAt',
          type: 'date',
          filterable: false,
          transformValue: (cellData) => new Date(cellData).toLocaleString(),
        },
        {
          title: 'Actions',
          property: 'actions',
          filterable: false,
          orderable: false,
          className: 'text-center',
          width: '10%',
          angularComponent: TableRowActionsComponent,
          angularComponentData: (row) => ({
            input: {
              rowData: row,
              actions: [
                {
                  ...EDIT,
                  onClick: () => this.router.navigate([`/app/groups/edit/${row.id}`]),
                },
                {
                  ...DELETE,
                  onClick: () => this.promptConfirmDeleteGroup(row.id),
                },
              ] as RowAction[],
            },
          }),
        },
      ],
    };
  });

  promptConfirmDeleteGroup(id: number) {
    const dialog = this.dialogService.openConfirmationDialog(
      'groups.deleteConfirmation.title',
      'groups.deleteConfirmation.message',
    );

    dialog.afterClosed().subscribe((response) => {
      if (response) {
        this.groupService.deleteGroup(id).subscribe({
          next: () => {
            console.log('Successfully deleted group with id:', id);
          },
          error: (error) => {
            console.log('Error deleting group with id:', id, error);
          },
        });
      }
    });
  }
}
