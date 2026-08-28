export interface PaginatedResponse<T> {
  content: T[];

  empty: boolean;
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  pageable: Pageable;
  size: number;
  sort: PaginatedSortOptions;
  totalElements: number;
  totalPages: number;
}

interface PaginatedSortOptions {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

interface Pageable {
  offset: number;
  pageNumber: number;
  pageSize: number;
  paged: boolean;
  sort: PaginatedSortOptions;
  unpaged: boolean;
}
