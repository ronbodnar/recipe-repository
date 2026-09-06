import { Component, computed, effect, input, output } from '@angular/core';
import { ButtonComponent } from '../button/button.component';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { SelectOption, InputSelectComponent } from '@ng-modular-forms/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-pagination',
  imports: [ReactiveFormsModule, ButtonComponent, InputSelectComponent, TranslatePipe],
  templateUrl: './pagination.component.html',
})
export class PaginationComponent {
  readonly totalItems = input<number>(0);
  readonly totalPages = input<number>(0);
  readonly currentPage = input<number>(0);
  readonly pageSize = input.required<number>();
  readonly pageSizeOptions = input.required<number[]>();

  readonly pageChange = output<number>();
  readonly pageSizeChange = output<number>();

  readonly pageSizeControl = new FormControl<number>(15, { nonNullable: true });

  readonly pageSizeSelectOptions = computed<SelectOption[]>(() =>
    this.pageSizeOptions().map((pageSize) => ({ value: pageSize, label: pageSize })),
  );

  constructor() {
    effect(() => {
      this.pageSizeControl.setValue(this.pageSize(), { emitEvent: false });
    });

    this.pageSizeControl.valueChanges.subscribe((pageSize) => {
      if (pageSize !== this.pageSize()) {
        this.pageSizeChange.emit(pageSize);
      }
    });
  }

  readonly pages = computed<(number | null)[]>(() => {
    const totalPages = this.totalPages();
    const currentPage = this.currentPage();

    if (totalPages <= 7) {
      return Array.from({ length: totalPages }, (_, page) => page);
    }

    const visiblePages = new Set([0, totalPages - 1]);
    for (
      let page = Math.max(1, currentPage - 1);
      page <= Math.min(totalPages - 2, currentPage + 1);
      page++
    ) {
      visiblePages.add(page);
    }

    const pages: (number | null)[] = [];
    let previousPage: number | null = null;
    for (const page of [...visiblePages].sort((firstPage, secondPage) => firstPage - secondPage)) {
      if (previousPage !== null && page > previousPage + 1) {
        pages.push(null);
      }
      pages.push(page);
      previousPage = page;
    }

    return pages;
  });

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages() && page !== this.currentPage()) {
      this.pageChange.emit(page);
    }
  }
}
