import { ComponentType } from '@angular/cdk/portal';
import { Observable } from 'rxjs';
import { Signal } from '@angular/core';
import { SelectOption } from '@ng-modular-forms/core';
import { PaginatedResponse } from '@core/interfaces/paginated-response.interface';

export interface PaginatedTableFetchParams {
  searchValue: string;
  searchColumn: string;
  page: number;
  pageSize: number;
  sort: string[];
}

export interface TableColumn<T> {
  property: string;

  /**
   * If the property is a nested object, this property specifies the path to the property on the backend.
   * For example, if the property is "clientFullName", the propertyPath could be "client.fullName".
   */
  propertyPath?: string;
  sortPropertyPath?: string;

  type?: string;
  title?: string;
  width?: string;

  sticky?: boolean;
  stickyEnd?: boolean;
  visible?: boolean;
  orderable?: boolean;

  filterable?: boolean;
  filterSelectOptions?: SelectOption[];
  filterDateRange?: boolean;

  className?: string;

  transformValue?: (cellData: any, rowData: T) => string | number | undefined;

  angularComponent?: ComponentType<any>;
  angularComponentData?: Record<string, any> | ((row: T) => Record<string, any>);
  componentWrapperClassList?: string;

  selectOptions?: SelectOption[];
}

export interface TableSort {
  property: string;
  direction: 'asc' | 'desc';
}

export interface PaginatedTableConfig<T> {
  columns?: TableColumn<T>[];

  filterable?: boolean; // Enable or disable search controls for all columns
  orderable?: boolean; // Enable or disable sorting for all columns
  pagination?: boolean; // Enable or disable pagination

  info?: boolean; // number of records (bottom left)
  paging?: boolean; // pagination (bottom right)
  lengthChange?: boolean; // changing number of rows per page (top left)
  serverSide?: boolean; // server-side processing
  processing?: boolean; // loading indicator overlay
  loadingDisplay?: boolean; // loading message / spinner overlay

  rawData?: T[];
  dataSignal?: Signal<T[] | undefined>;
  fetchData?: TableFetchData<T>;

  defaultSort?: TableSort;

  onRowClick?: (rowData: any) => void;

  routerLink?: string;
  routerState?: Record<string, any>;

  // Styles
  small?: boolean;
  autoSize?: boolean;
  elevation?: number;
  styleRowsFromStatus?: boolean;
}

export type TableFetchData<T> = (params: any) => Observable<PaginatedResponse<T>>;
