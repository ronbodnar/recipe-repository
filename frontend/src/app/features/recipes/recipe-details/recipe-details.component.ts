import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Recipe } from '../recipe.types';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { TranslatePipe } from '@ngx-translate/core';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { MatIconModule } from '@angular/material/icon';
import { RichTextListComponent } from '../components/rich-text-list/rich-text-list.component';
import { DialogService } from '@shared/ui/dialog/dialog.service';
import { RecipeService } from '../recipe.service';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import { ImageService } from '@core/services/image.service';

@Component({
  selector: 'app-recipe-details',
  imports: [
    FluidContainerComponent,
    FullPageLoaderComponent,
    TranslatePipe,
    ButtonComponent,
    MatIconModule,
    RichTextListComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './recipe-details.component.html',
})
export class RecipeDetailsComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly dialogService = inject(DialogService);
  private readonly recipeService = inject(RecipeService);
  private readonly snackbarService = inject(SnackbarService);
  readonly imageService = inject(ImageService);

  private readonly _recipe = signal<Recipe | null>(null);
  private readonly _loading = signal(true);
  private readonly _deleting = signal(false);
  private readonly _hasError = signal(false);
  private readonly _selectedVariantIndex = signal<number | null>(null);

  readonly recipe = this._recipe.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly hasError = this._hasError.asReadonly();
  readonly deleting = this._deleting.asReadonly();
  readonly selectedVariantIndex = this._selectedVariantIndex.asReadonly();
  readonly selectedVariant = computed(
    () => this.recipe()?.variants[this.selectedVariantIndex() ?? 0] ?? null,
  );

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this._hasError.set(true);
      this._loading.set(false);
      return;
    }

    this.recipeService.loadRecipe(id).subscribe({
      next: (recipe) => {
        console.log('Fetched recipe detail:', recipe);

        const recipeSortedVariants = recipe.variants.sort((a, b) => {
          const methodA = a.name ?? a.cookingMethod ?? '';
          const methodB = b.name ?? b.cookingMethod ?? '';
          return methodA.localeCompare(methodB);
        });
        recipe.variants = recipeSortedVariants;

        if (recipe.variants.length > 0) {
          this._selectedVariantIndex.set(0);
        }

        this._recipe.set(recipe);
        this._hasError.set(false);
        this._loading.set(false);
      },
      error: (error) => {
        console.error('Failed to fetch recipe detail:', error);
        this._hasError.set(true);
        this._loading.set(false);
      },
    });
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
    this.dialogService.openConfirmationDialog(
      'recipes.details.deleteConfirmation.title',
      'recipes.details.deleteConfirmation.message',
      (confirmed) => {
        if (confirmed) {
          this.deleteRecipe();
        }
      },
    );
  }

  deleteRecipe(): void {
    const recipeId = this.recipe()?.id;
    if (!recipeId) {
      console.error('No recipe ID available for deletion.');
      return;
    }

    console.log('Deleting recipe with ID:', recipeId);

    this._deleting.set(true);
    this.recipeService.deleteRecipe(recipeId).subscribe({
      next: () => {
        console.log('Recipe deleted successfully.');
        this.router.navigate(['/app/recipes/list']);
      },
      error: (error) => {
        console.error('Failed to delete recipe:', error);
        this._deleting.set(false);
        this.snackbarService.openSnackBar(SnackbarType.ERROR, 'recipes.details.deleteError');
      },
    });
  }
}
