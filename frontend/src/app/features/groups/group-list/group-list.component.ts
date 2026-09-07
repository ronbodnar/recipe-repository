import { Component, computed, inject } from '@angular/core';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { PaginatedTableConfig } from '@shared/ui/paginated-table/paginated-table.config';
import { PaginatedTableComponent } from '@shared/ui/paginated-table/paginated-table.component';
import { FetchApiService } from '@core/services/fetch-api.service';
import { ButtonComponent } from '@shared/ui/button/button.component';

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
        },
        {
          title: 'Created At',
          property: 'createdAt',
          type: 'date',
          filterable: false,
        },
      ],
    };
  });
}
