import { effect, Injectable, Injector, signal, Signal, inject } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import {
  Subscription,
  distinctUntilChanged,
  debounceTime,
  merge,
  catchError,
  EMPTY,
  skip,
  tap,
} from 'rxjs';
import { PaginatedTableConfig, PaginatedTableFetchParams } from '../paginated-table.config';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { PaginatedTableStore } from '../paginated-table.store';
import { enrichData } from '../util/enrich-data.util';
import { PaginatedTableSearchService } from './paginated-table-search.service';
import { sortData } from '../util/sort-data.util';
import { PaginatedResponse } from '@core/interfaces/paginated-response.interface';

@Injectable()
export class PaginatedTableService<T> {
  private injector = inject(Injector);
  private sanitizer = inject(DomSanitizer);
  private store = inject<PaginatedTableStore<T>>(PaginatedTableStore);
  private search = inject<PaginatedTableSearchService<T>>(PaginatedTableSearchService);

  private paginationSubscription!: Subscription;

  private sort!: MatSort;
  private paginator!: MatPaginator;

  private _fetchResponse = signal<PaginatedResponse<T> | undefined>(undefined);

  public readonly fetchResponse = this._fetchResponse.asReadonly();

  setup(config: Signal<PaginatedTableConfig<T>>, sort: MatSort, paginator: MatPaginator): void {
    // Initialize config and store data
    this.store.setConfig(config);
    this.store.setVisibleColumns(config().columns?.filter((c) => c.visible !== false) || []);
    this.store.setVisibleColumnNames(
      this.store.visibleColumns().map((c) => c.propertyPath || c.property || ''),
    );

    this.sort = sort;
    this.sort.active = this.store.config().defaultSort?.property || '';
    this.sort.direction = this.store.config().defaultSort?.direction || '';

    this.paginator = paginator;

    this.wireReactiveLogic();
  }

  destroy(): void {
    this.paginationSubscription?.unsubscribe();
  }

  wireReactiveLogic(): void {
    this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));

    /*
     * Wire up listeners for sort headers and page size or index changes.
     */
    this.paginationSubscription = merge(
      this.sort.sortChange,
      this.paginator?.page || EMPTY,
    ).subscribe(() => this.loadData(true));

    toObservable(this.store.filterValue, { injector: this.injector })
      .pipe(
        skip(1),
        distinctUntilChanged(),
        debounceTime(1000),
        tap(() => (this.paginator.pageIndex = 0)),
      )
      .subscribe(() => this.loadData(true));

    // This is likely abusing Angular's intentions for effect
    // Later, refactor to use a computed signal on renderedData in store.
    // This causes re-rendering when searching on dataSignal data source tables.
    // You can only type 1 character and it will re-render/loadData and glitchy.
    effect(
      () => {
        const config = this.store.config();
        const dataSource = config?.rawData || config?.dataSignal?.() || config?.fetchData;
        if (dataSource) {
          this.loadData();
        }
      },
      { injector: this.injector },
    );
  }

  setData(data: T[], totalItems?: number) {
    if (!data) {
      return;
    }
    const enrichedData = enrichData<T>(this.store.visibleColumns(), data);

    const dataLength = enrichedData.length;
    const dataExceedsPageSize = dataLength > (this.paginator?.pageSize || 0);

    this.store.setLoaded(true);
    this.store.setReloading(false);
    this.store.setTotalItems(totalItems || dataLength);

    if (this.paginator && dataExceedsPageSize) {
      const start = this.paginator.pageIndex * this.paginator.pageSize;
      const end = (this.paginator.pageIndex + 1) * this.paginator.pageSize;
      const pageData = enrichedData.slice(start, Math.min(end, dataLength));
      this.store.setRenderedData(this.search.injectSearchRow(pageData));
    } else {
      this.store.setRenderedData(this.search.injectSearchRow(enrichedData));
    }
  }

  filterData(data: T[]): T[] {
    const filterColumn: keyof T = this.store.filterColumn() as keyof T;
    const filterValue = this.store.filterValue();
    if (filterColumn && filterValue) {
      return data.filter((row: T) => {
        const value = row[filterColumn];
        if (value == null) {
          return false;
        }
        return value.toString().toLowerCase().includes(filterValue.toLowerCase());
      });
    }
    return data;
  }

  loadData(triggeredFromSearch = false): void {
    console.log('loadData');
    const config = this.store.config();
    if (!config) {
      return;
    }

    this.store.setReloading(true);

    if (config.rawData) {
      const { active, direction } = this.sort;
      if (active && direction) {
        config.rawData = sortData(config.rawData, active as keyof T, direction);
      }

      if (triggeredFromSearch) {
        config.rawData = this.filterData(config.rawData);
      }

      this.setData(config.rawData);
      return;
    }

    if (config.dataSignal?.()) {
      const { active, direction } = this.sort;
      let data = config.dataSignal()!;
      if (active && direction) {
        data = sortData(config.dataSignal()!, active as keyof T, direction);
      }

      if (triggeredFromSearch) {
        data = this.filterData(data);
      }

      this.setData(data);
      return;
    }

    if (config.fetchData) {
      const { pageIndex, pageSize } = this.paginator;
      const { active, direction } = this.sort;

      const params: PaginatedTableFetchParams = {
        searchValue: triggeredFromSearch ? this.store.filterValue() : '',
        searchColumn: triggeredFromSearch ? this.store.filterColumn() : '',
        page: pageIndex * pageSize,
        pageSize: pageSize,
        sortBy: ['createdAt,desc'],
      };

      if (active && direction) {
        params.sortBy = [active + ',' + direction];
      }

      config
        .fetchData(params)
        .pipe(
          catchError(() => {
            this.store.setReloading(false);
            return EMPTY;
          }),
        )
        .subscribe((response) => {
          console.log('fetchData response:', response);
          if (!response) {
            return;
          }
          if (!response.content) {
            console.error('Unexpected response from fetchData:', response);
            return;
          }
          this._fetchResponse.set(response);
          this.setData(response.content, response.page.totalElements);
        });
    }
  }

  sanitizeHtmlContent(html: string | null): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(html || '');
  }
}
