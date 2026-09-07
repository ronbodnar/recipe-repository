import { inject, Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';

@Injectable()
export class PaginatedTablePaginatorIntl implements MatPaginatorIntl {
  changes = new Subject<void>();

  translateService = inject(TranslateService);

  firstPageLabel = this.translateService.instant('ui.tables.paginator.firstPage');
  itemsPerPageLabel = this.translateService.instant('ui.tables.paginator.itemsPerPage');
  lastPageLabel = this.translateService.instant('ui.tables.paginator.lastPage');
  nextPageLabel = this.translateService.instant('ui.tables.paginator.nextPage');
  previousPageLabel = this.translateService.instant('ui.tables.paginator.previousPage');
  ofTranslated = this.translateService.instant('ui.tables.paginator.of');
  pageTranslated = this.translateService.instant('ui.tables.paginator.page');

  getRangeLabel(page: number, pageSize: number, length: number): string {
    const amountPages = Math.ceil(length / pageSize);
    return `${this.pageTranslated} ${amountPages === 0 ? 0 : page + 1} ${this.ofTranslated} ${amountPages}`;
  }
}
