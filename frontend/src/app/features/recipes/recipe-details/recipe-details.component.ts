import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Recipe } from '../recipe.types';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { MatIconModule } from '@angular/material/icon';
import { RichTextListComponent } from '../components/rich-text-list/rich-text-list.component';
import { DialogService } from '@shared/ui/dialog/dialog.service';
import { RecipeService } from '../recipe.service';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import { ImageService } from '@core/services/image.service';
import { CommonModule, DatePipe, LowerCasePipe } from '@angular/common';
import { UserService } from '@features/users/user.service';
import { UserAccountSummary } from '@features/users/user.types';
import { AuthenticationService } from '@core/services/authentication.service';
import { MatTabsModule } from '@angular/material/tabs';
import { ImageCarouselComponent } from '@shared/ui/image-carousel/image-carousel.component';
import { RecipeNotFoundComponent } from '../components/recipe-not-found/recipe-not-found.component';
import { RecipeListSource } from '../recipe-list/recipe-list.component';
import { logDebug } from '@shared/utils/logging';

@Component({
  selector: 'app-recipe-details',
  imports: [
    CommonModule,
    FluidContainerComponent,
    FullPageLoaderComponent,
    TranslatePipe,
    ButtonComponent,
    MatIconModule,
    RichTextListComponent,
    DatePipe,
    LowerCasePipe,
    MatTabsModule,
    ImageCarouselComponent,
    RecipeNotFoundComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './recipe-details.component.html',
})
export class RecipeDetailsComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly translate = inject(TranslateService);
  private readonly dialogService = inject(DialogService);
  private readonly recipeService = inject(RecipeService);
  private readonly snackbarService = inject(SnackbarService);
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthenticationService);
  readonly imageService = inject(ImageService);

  private readonly _recipe = signal<Recipe | null>(null);
  private readonly _author = signal<UserAccountSummary | null>(null);
  private readonly _loading = signal(true);
  private readonly _deleting = signal(false);
  private readonly _hasError = signal(false);
  private readonly _source = signal<RecipeListSource | null>(null);
  private readonly _selectedVariantIndex = signal<number | null>(null);

  readonly recipe = this._recipe.asReadonly();
  readonly author = this._author.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly hasError = this._hasError.asReadonly();
  readonly deleting = this._deleting.asReadonly();
  readonly source = this._source.asReadonly();
  readonly selectedVariantIndex = this._selectedVariantIndex.asReadonly();

  readonly selectedVariant = computed(
    () => this.recipe()?.variants[this.selectedVariantIndex() ?? 0] ?? null,
  );

  readonly recipeAttributes = computed(() => {
    const recipe = this.recipe();
    if (!recipe) {
      return [];
    }

    const translateAndSort = (items: string[], keyPrefix: string) =>
      items
        .map((item) =>
          this.translate.instant(
            `recipes.attributes.${keyPrefix}.${item.toLowerCase().replaceAll('_', '-')}`,
          ),
        )
        .sort((a, b) => a.localeCompare(b));

    return [
      {
        label: 'recipes.labels.cuisines',
        icon: 'public',
        values: translateAndSort(recipe.cuisines, 'cuisines'),
      },
      {
        label: 'recipes.labels.courses',
        icon: 'restaurant',
        values: translateAndSort(recipe.courses, 'courses'),
      },
      {
        label: 'recipes.labels.mealTypes',
        icon: 'wb_sunny',
        values: translateAndSort(recipe.mealTypes, 'mealTypes'),
      },
      {
        label: 'recipes.labels.dietTypes',
        icon: 'eco',
        values: translateAndSort(recipe.dietTypes, 'dietTypes'),
      },
    ].filter((attribute) => attribute.values.length > 0);
  });

  protected readonly sourceUrl = computed(() => {
    const source = this.recipe()?.source;

    if (!source) {
      return null;
    }

    if (/^https?:\/\//i.test(source)) {
      return source;
    }

    if (/^www\./i.test(source)) {
      return `https://${source}`;
    }

    return null;
  });

  readonly isRecipeOwner = computed(() => {
    const currentUser = this.authService.authUser();
    return currentUser?.id === this.recipe()?.authorId;
  });

  constructor() {
    const state = this.router.currentNavigation()?.extras.state as
      | { source?: RecipeListSource; recipe?: Recipe }
      | undefined;

    const { source, recipe } = state ?? {};

    if (source) {
      this._source.set(source);
    }

    if (recipe) {
      this._recipe.set(recipe);
      if (recipe.variants.length > 0) {
        this._selectedVariantIndex.set(0);
      }
      this.loadAuthor(recipe.authorId);
    }
  }

  ngOnInit(): void {
    const recipeId = this.route.snapshot.paramMap.get('id');

    if (!recipeId) {
      this._hasError.set(true);
      this._loading.set(false);
      return;
    }

    if (!this.recipe()) {
      this.loadRecipe(recipeId);
    }
  }

  getVariantLabel(index: number) {
    const variant = this.recipe()?.variants[index] ?? null;
    const method = variant?.cookingMethod ?? null;
    return `recipes.labels.cookingMethods.${method?.toLowerCase().replaceAll('_', '-')}`;
  }

  selectVariant(index: number): void {
    this._selectedVariantIndex.set(index);
  }

  promptDeleteRecipe(): void {
    const dialog = this.dialogService.openConfirmationDialog(
      'recipes.details.deleteConfirmation.title',
      'recipes.details.deleteConfirmation.message',
    );

    dialog.afterClosed().subscribe((response) => {
      if (response) {
        this.deleteRecipe();
      }
    });
  }

  deleteRecipe(): void {
    const recipeId = this.recipe()?.id;
    if (!recipeId) {
      logDebug('No recipe ID available for deletion.');
      return;
    }

    this._deleting.set(true);

    this.recipeService.deleteRecipe(recipeId).subscribe({
      next: () => {
        this.router.navigate(['/app/recipes/list']);
      },
      error: (error) => {
        logDebug('An error occurred while deleting this recipe:', error);
        this._deleting.set(false);
        this.snackbarService.openSnackBar(SnackbarType.ERROR, 'recipes.details.deleteError');
      },
    });
  }

  goBack(): void {
    this.router.navigate(
      this.source() === 'discover' ? ['/app/recipes/discover'] : ['/app/recipes/list'],
    );
  }

  private loadRecipe(recipeId: string) {
    this.recipeService.loadRecipe(recipeId).subscribe({
      next: (recipe) => {
        logDebug('Fetched recipe detail:', recipe);

        if (recipe.variants.length > 0) {
          this._selectedVariantIndex.set(0);
        }

        this.loadAuthor(recipe.authorId);

        this._recipe.set(recipe);
      },
      error: (error) => {
        logDebug('Failed to fetch recipe detail:', error);
        this._hasError.set(true);
        this._loading.set(false);
      },
    });
  }

  private loadAuthor(id: number) {
    this.userService.loadUserSummary(id).subscribe({
      next: (user) => {
        logDebug('Fetched author details:', user);
        this._author.set(user);
        this._hasError.set(false);
        this._loading.set(false);
      },
      error: (error) => {
        logDebug('Failed to fetch author details:', error);
        this._hasError.set(true);
        this._loading.set(false);
      },
    });
  }
}
