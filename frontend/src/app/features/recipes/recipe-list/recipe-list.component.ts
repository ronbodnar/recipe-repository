import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { RecipeSummary } from '../recipe.types';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';
import { RecipeService } from '../recipe.service';
import { RecipeCardListComponent } from '../components/recipe-card-list/recipe-card-list.component';
import { MatIconModule } from '@angular/material/icon';
import { logDebug } from '@shared/utils/logging';
import { PaginatedResponse } from '@core/interfaces/paginated-response.interface';

export type RecipeListSource = 'my-recipes' | 'discover';

@Component({
  selector: 'app-recipe-list',
  imports: [
    CommonModule,
    RouterLink,
    FluidContainerComponent,
    MatIconModule,
    ButtonComponent,
    TranslatePipe,
    FullPageLoaderComponent,
    RecipeCardListComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './recipe-list.component.html',
  styleUrl: './recipe-list.component.css',
})
export class RecipeListComponent {
  private recipeService = inject(RecipeService);
  private translate = inject(TranslateService);
  private route = inject(ActivatedRoute);

  private _source = signal<RecipeListSource>('my-recipes');
  private _recipeResponse = signal<PaginatedResponse<RecipeSummary> | null>(null);
  private _isLoading = signal(true);
  private _hasError = signal(false);

  readonly source = this._source.asReadonly();
  readonly recipeResponse = this._recipeResponse.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly hasError = this._hasError.asReadonly();

  readonly appName = this.translate.instant('app.name');

  readonly loadedRecipes = computed(() => this.recipeResponse()?.content || []);
  readonly totalRecipes = computed(() => this.recipeResponse()?.page.totalElements || 0);
  readonly totalPages = computed(() => this.recipeResponse()?.page.totalPages || 0);

  readonly pageTitle = computed(() => {
    const source = this.source();
    const loadedRecipes = this.recipeResponse()?.content;

    if (!loadedRecipes || loadedRecipes.length === 0) {
      return '';
    }

    switch (source) {
      case 'my-recipes':
        return 'recipes.list.my-recipes.title';

      case 'discover':
        return 'recipes.list.discover.title';

      default:
        return '';
    }
  });

  pageSize = 1;
  page = 0;

  ngOnInit(): void {
    const source = this.route.snapshot.data['source'];
    this._source.set(source);
    this.loadPage(this.page);
  }

  loadPage(page: number): void {
    this.page = page;
    this._isLoading.set(true);

    const recipes$ =
      this.source() === 'discover'
        ? this.recipeService.loadDiscoverRecipes(this.page, this.pageSize)
        : this.recipeService.loadPaginatedRecipes(this.page, this.pageSize);

    recipes$.subscribe({
      next: (data) => {
        logDebug('Fetched recipes', data);
        this._recipeResponse.set(data);
        this._hasError.set(false);
        this._isLoading.set(false);
      },
      error: (error) => {
        logDebug('Failed to fetch recipes:', error);
        this._hasError.set(true);
        this._isLoading.set(false);
      },
    });
  }
}
