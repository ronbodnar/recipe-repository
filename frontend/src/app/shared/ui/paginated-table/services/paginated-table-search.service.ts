import { computed, Injectable, inject } from '@angular/core';
import { TableColumn } from '../paginated-table.config';
import { PaginatedTableStore } from '../paginated-table.store';
import { MatSelectChange } from '@angular/material/select';

@Injectable()
export class PaginatedTableSearchService<T> {
  private store = inject<PaginatedTableStore<T>>(PaginatedTableStore);

  filter(event: Event | MatSelectChange, column: TableColumn<T>) {
    let value = '';
    if (event instanceof MatSelectChange) {
      value = event?.value;
    } else if (event instanceof Event) {
      value = (event.target as any)?.value;
    }
    const filterValue = value;
    const filterProperty = column.propertyPath || column.property;
    this.store.setFilterValue(filterValue);
    if (this.store.filterColumn() !== filterProperty) {
      this.store.setFilterColumn(filterProperty);
    }
  }

  renderFilterRow = (rowData?: any) => rowData?.type === 'search';

  readonly hasFilterRow = computed(() => {
    const config = this.store.config();
    return (
      this.store.filterValue() ||
      (this.store.totalItems() > 0 &&
        (config?.filterable !== false || !!config?.columns?.find((column) => column.filterable)))
    );
  });

  injectSearchRow(data: T[]): any[] {
    return this.hasFilterRow() ? [{ type: 'search' }, ...data] : data;
  }
}
