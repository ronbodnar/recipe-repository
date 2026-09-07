import { TableColumn } from '../paginated-table.config';
import { bindValue, getPropertyValue } from './paginated-table.utils';

export function enrichData<T>(
  visibleColumns: TableColumn<T>[],
  data: T[],
): Array<T & { __rendered__: any; __componentInputs__: any }> {
  return data.map((row: T) => {
    return {
      ...row,
      __rendered__: buildRenderedMap(visibleColumns, row),
      __componentInputs__: buildComponentInputs(visibleColumns, row),
      __componentOutputs__: buildComponentOutputs(visibleColumns, row),
    };
  });
}

function buildRenderedMap<T>(
  visibleColumns: TableColumn<T>[],
  row: T,
): Array<string | number | undefined> {
  return visibleColumns?.map((col) => {
    const colValue = getPropertyValue(row, col.property);

    return String(col.transformValue?.(colValue, row) ?? colValue);
  });
}

function buildComponentInputs<T>(visibleColumns: TableColumn<T>[], row: T): any[] {
  return visibleColumns.map((col) => {
    const data = resolveAngularComponentData(col.angularComponentData, row);
    return data?.['input'] ?? undefined;
  });
}

function buildComponentOutputs<T>(visibleColumns: TableColumn<T>[], row: T): any[] {
  return visibleColumns.map((col) => {
    const data = resolveAngularComponentData(col.angularComponentData, row);
    return data?.['output'] ?? undefined;
  });
}

function resolveAngularComponentData<T>(
  data: Record<string, unknown> | ((row: T) => Record<string, unknown>) | undefined,
  row: T,
): Record<string, unknown> | undefined {
  if (!data) return undefined;
  const resolved = typeof data === 'function' ? data(row) : bindValue(data, row);
  return resolved;
}
