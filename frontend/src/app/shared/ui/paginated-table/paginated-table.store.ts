import { computed, Injectable, Signal, signal } from '@angular/core';
import { PaginatedTableConfig, TableColumn } from './paginated-table.config';

@Injectable()
export class PaginatedTableStore<T> {
  private _config!: Signal<PaginatedTableConfig<T>>;
  private _loaded = signal<boolean>(false);
  private _pageSize = signal<number>(10);
  private _reloading = signal<boolean>(false);
  private _filterValue = signal<string>('');
  private _filterColumn = signal<string>('');
  private _totalItems = signal<number>(0);
  private _renderedData = signal<T[] | undefined>(undefined);
  private _visibleColumns = signal<TableColumn<T>[]>([]);
  private _visibleColumnNames = signal<string[]>([]);

  public readonly config = computed(() => this._config());
  public readonly loaded = computed(() => this._loaded());
  public readonly pageSize = computed(() => this._pageSize());
  public readonly reloading = computed(() => this._reloading());
  public readonly filterValue = computed(() => this._filterValue());
  public readonly filterColumn = computed(() => this._filterColumn());
  public readonly totalItems = computed(() => this._totalItems());
  public readonly renderedData = computed(() => this._renderedData());
  public readonly visibleColumns = computed(() => this._visibleColumns());
  public readonly visibleColumnNames = computed(() => this._visibleColumnNames());

  public setConfig(config: Signal<PaginatedTableConfig<T>>): void {
    this._config = config;
  }

  public setLoaded(loaded: boolean): void {
    this._loaded.set(loaded);
  }

  public setPageSize(pageSize: number): void {
    this._pageSize.set(pageSize);
  }

  public setReloading(reloading: boolean): void {
    this._reloading.set(reloading);
  }

  public setFilterValue(filterValue: string): void {
    this._filterValue.set(filterValue);
  }

  public setFilterColumn(filterColumn: string): void {
    this._filterColumn.set(filterColumn);
  }

  public setTotalItems(totalItems: number): void {
    this._totalItems.set(totalItems);
  }

  public setRenderedData(renderedData: T[] | undefined): void {
    this._renderedData.set(renderedData);
  }

  public setVisibleColumns(visibleColumns: TableColumn<T>[]): void {
    this._visibleColumns.set(visibleColumns);
  }

  public setVisibleColumnNames(visibleColumnNames: string[]): void {
    this._visibleColumnNames.set(visibleColumnNames);
  }
}
