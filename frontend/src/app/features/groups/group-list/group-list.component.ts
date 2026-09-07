import { Component, computed, inject } from '@angular/core';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { PaginatedTableConfig } from '@shared/ui/paginated-table/paginated-table.config';
import { PaginatedTableComponent } from '@shared/ui/paginated-table/paginated-table.component';
import { FetchApiService } from '@core/services/fetch-api.service';
import { ButtonComponent } from '@shared/ui/button/button.component';
import {
  DELETE,
  EDIT,
  RowAction,
  TableRowActionsComponent,
} from '@shared/ui/paginated-table/components/table-row-actions/table-row-actions.component';
import { TableLinkComponent } from '@shared/ui/paginated-table/components/table-link/table-link.component';

@Component({
  selector: 'app-group-list',
  imports: [FluidContainerComponent, PaginatedTableComponent, ButtonComponent],
  templateUrl: './group-list.component.html',
})
export class GroupListComponent {
  private readonly fetchApi = inject(FetchApiService);

  fetchData(parameters: Record<string, unknown>) {
    return this.fetchApi.fetchPaginatedData<any>(`identity/groups`, {
      parameters: {
        ...parameters,
      },
    });
  }

  tableConfig = computed<PaginatedTableConfig<any>>(() => {
    return {
      elevation: 4,
      pagination: true,
      filterable: true,
      orderable: true,
      fetchData: (params: Record<string, unknown>) => this.fetchData(params),
      columns: [
        {
          title: 'Name',
          property: 'name',
          width: '50%',
          angularComponent: TableLinkComponent,
          angularComponentData: (row) => ({
            input: {
              label: row.name,
              routerLink: `/groups/${row.id}`,
              target: '_self',
            },
          }),
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
                  onClick: () => {
                    console.log('Edit clicked for group:');
                  },
                },
                {
                  ...DELETE,
                  onClick: () => {
                    console.log('Delete clicked for group:');
                  },
                },
              ] as RowAction[],
            },
          }),
        },
      ],
    };
  });
}
