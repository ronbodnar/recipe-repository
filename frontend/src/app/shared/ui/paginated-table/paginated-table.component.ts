import {
  Component,
  ViewChild,
  AfterViewInit,
  ChangeDetectionStrategy,
  Input,
  Output,
  EventEmitter,
  Signal,
  OnDestroy,
  computed,
  effect,
  inject,
} from '@angular/core';
import {
  MatPaginator,
  MatPaginatorIntl,
  MatPaginatorModule,
  PageEvent,
} from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';
import { PaginatedTablePaginatorIntl } from './services/paginated-table-paginator-intl.service';
import { MatInputModule } from '@angular/material/input';
import { PaginatedTableService } from './services/paginated-table.service';
import { PaginatedTableConfig, TableColumn } from './paginated-table.config';
import { PaginatedTableStore } from './paginated-table.store';
import { SafeHtml } from '@angular/platform-browser';
import { PaginatedTableSearchService as PaginatedTableFilterService } from './services/paginated-table-search.service';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { getPropertyValue } from './util/paginated-table.utils';
import { TranslatePipe } from '@ngx-translate/core';
import { ToStringPipe } from '@shared/pipes/to-string.pipe';
import { MatDividerModule } from '@angular/material/divider';
import { ComponentWrapperComponent } from '../dialog/components/component-wrapper.component';
import { ScopedLoaderComponent } from '../scoped-loader.component';
import { InputSelectComponent, InputTextComponent } from '@ng-modular-forms/core';

@Component({
  selector: 'app-paginated-table',
  imports: [
    CommonModule,
    MatInputModule,
    MatSelectModule,
    MatDividerModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    ComponentWrapperComponent,
    ScopedLoaderComponent,
    TranslatePipe,
    ToStringPipe,
    InputSelectComponent,
    InputTextComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './paginated-table.component.css',
  templateUrl: './paginated-table.component.html',
  providers: [
    { provide: MatPaginatorIntl, useClass: PaginatedTablePaginatorIntl },
    PaginatedTableStore,
    PaginatedTableService,
    PaginatedTableFilterService,
  ],
})
export class PaginatedTableComponent<T = any> implements AfterViewInit, OnDestroy {
  private store = inject<PaginatedTableStore<T>>(PaginatedTableStore);
  private service = inject<PaginatedTableService<T>>(PaginatedTableService);
  private filterService = inject<PaginatedTableFilterService<T>>(PaginatedTableFilterService);

  @Input() tableConfigSignal!: Signal<PaginatedTableConfig<T>>;

  @Output() rowClick: EventEmitter<any> = new EventEmitter();
  @Output() dataResponse: EventEmitter<any> = new EventEmitter();

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  readonly loaded = this.store.loaded;
  readonly pageSize = this.store.pageSize;
  readonly reloading = this.store.reloading;
  readonly totalItems = this.store.totalItems;
  readonly renderedData = this.store.renderedData;
  readonly filterValue = this.store.filterValue;
  readonly filterColumn = this.store.filterColumn;
  readonly visibleColumns = this.store.visibleColumns;
  readonly visibleColumnNames = this.store.visibleColumnNames;
  readonly totalDisplayedItems = computed(() => {
    const data = this.renderedData();
    if (!data?.length) {
      return 0;
    }
    const hasSearchRow = (data as any)[0].type === 'search';

    return hasSearchRow ? data.length - 1 : data.length;
  });

  constructor() {
    effect(() => {
      this.dataResponse.emit(this.service.fetchResponse());
    });
  }

  ngAfterViewInit(): void {
    this.service.setup(this.tableConfigSignal, this.sort, this.paginator);
  }

  ngOnDestroy(): void {
    this.service.destroy();
  }

  pagination = computed(() => this.tableConfigSignal().pagination !== false);

  handleRowClick(event: MouseEvent, rowData: any) {
    this.rowClick.emit(rowData);
  }

  handlePageChange(event: PageEvent) {
    this.store.setPageSize(event.pageSize);
  }

  renderFilterRow = (rowData?: any) => this.filterService.renderFilterRow(rowData);

  filter(event: Event | MatSelectChange, column: TableColumn<T>) {
    this.filterService.filter(event, column);
  }

  sanitizeHtmlContent(html: string | null): SafeHtml {
    return this.service.sanitizeHtmlContent(html);
  }

  getPropertyValue(obj: any, path: string): string {
    return String(getPropertyValue(obj, path));
  }
}
