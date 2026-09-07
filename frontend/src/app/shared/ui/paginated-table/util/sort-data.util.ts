export function sortData<T>(data: T[], active: keyof T, direction: 'asc' | 'desc'): T[] {
  const sorted = [...data].sort((a: T, b: T) => {
    const key = active as keyof T;
    const aValue = a[key];
    const bValue = b[key];

    if (aValue == null && bValue != null) return direction === 'asc' ? 1 : -1;
    if (aValue != null && bValue == null) return direction === 'asc' ? -1 : 1;
    if (aValue == null && bValue == null) return 0;

    if (typeof aValue === 'string' && typeof bValue === 'string') {
      const compare = aValue.localeCompare(bValue);
      return direction === 'asc' ? compare : -compare;
    }

    if (typeof aValue === 'boolean' && typeof bValue === 'boolean') {
      const compare = aValue === bValue ? 0 : aValue ? -1 : 1;
      return direction === 'asc' ? compare : -compare;
    }

    const compare = aValue < bValue ? -1 : aValue > bValue ? 1 : 0;
    return direction === 'asc' ? compare : -compare;
  });
  return sorted;
}
