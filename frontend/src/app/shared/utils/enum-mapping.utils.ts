export function toBackendEnum<T extends string>(value: string | null | undefined): T {
  if (!value) {
    return '' as T;
  }

  return value
    .trim()
    .toUpperCase()
    .replace(/[^A-Z0-9]+/g, '_')
    .replace(/^_+|_+$/g, '') as T;
}

export function toFormEnum<T extends string>(
  backendValue: string | null | undefined,
  options: readonly T[],
): T | null {
  const normalized = toBackendEnum<T>(backendValue);
  return options.find((option) => toBackendEnum<T>(option) === normalized) ?? null;
}

export function toFormEnumArray<T extends string>(
  backendValues: string[],
  options: readonly T[],
): T[] {
  return backendValues
    .map((backendValue) => toFormEnum<T>(backendValue, options))
    .filter((value): value is T => value !== null);
}
