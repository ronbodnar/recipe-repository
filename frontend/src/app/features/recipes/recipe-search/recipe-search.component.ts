import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  computed,
  inject,
  signal,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { distinctUntilChanged, map } from 'rxjs';
import { TranslatePipe } from '@ngx-translate/core';
import { MatIconModule } from '@angular/material/icon';
import { RecipeService } from '../recipe.service';
import { RecipeSummary } from '../recipe.types';
import { PaginatedResponse } from '@core/interfaces/paginated-response.interface';
import { logDebug } from '@shared/utils/logging';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { ScopedLoaderComponent } from '@shared/ui/scoped-loader.component';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { PaginationComponent } from '@shared/ui/pagination/pagination.component';
import { RecipeCardListComponent } from '../components/recipe-card-list/recipe-card-list.component';

@Component({
  selector: 'app-recipe-search',
  imports: [
    ButtonComponent,
    FluidContainerComponent,
    MatIconModule,
    PaginationComponent,
    RecipeCardListComponent,
    ScopedLoaderComponent,
    TranslatePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './recipe-search.component.html',
})
export class RecipeSearchComponent {
  private readonly recipeService = inject(RecipeService);
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  private readonly _query = signal('');
  private readonly _recipeResponse = signal<PaginatedResponse<RecipeSummary> | null>(null);
  private readonly _isLoading = signal(false);
  private readonly _hasError = signal(false);

  readonly query = this._query.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly hasError = this._hasError.asReadonly();

  readonly loadedRecipes = computed(() => this._recipeResponse()?.content ?? []);
  readonly totalRecipes = computed(() => this._recipeResponse()?.page.totalElements ?? 0);
  readonly totalPages = computed(() => this._recipeResponse()?.page.totalPages ?? 0);

  readonly pageSizeOptions = [10, 15, 25, 50];
  pageSize = 15;
  currentPage = 0;

  constructor() {
    this.route.queryParamMap
      .pipe(
        map((params) => params.get('searchValue')?.trim() ?? ''),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((searchValue) => {
        this._query.set(searchValue);
        this.loadPage(0);
      });
  }

  loadPage(page: number): void {
    this.currentPage = page;
    const searchValue = this.query();

    if (!searchValue) {
      this._recipeResponse.set(null);
      this._hasError.set(false);
      this._isLoading.set(false);
      return;
    }

    this._isLoading.set(true);
    this._hasError.set(false);

    this.recipeService.searchRecipes(searchValue, page, this.pageSize).subscribe({
      next: (response) => {
        this._recipeResponse.set(response);
        this._isLoading.set(false);
      },
      error: (error) => {
        logDebug('Failed to search recipes:', error);
        this._hasError.set(true);
        this._isLoading.set(false);
      },
    });
  }

  changePageSize(pageSize: number): void {
    this.pageSize = pageSize;
    this.loadPage(0);
  }
}
