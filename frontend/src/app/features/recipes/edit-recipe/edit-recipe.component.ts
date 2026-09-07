import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormArray, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import {
  CdkDrag,
  CdkDragHandle,
  CdkDragDrop,
  CdkDropList,
  moveItemInArray,
} from '@angular/cdk/drag-drop';
import { MediaService } from '@core/services/media.service';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { RecipeForm, RecipeFormModel, RecipeVariantForm } from './edit-recipe.types';
import { RecipeService } from '../recipe.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { RecipeFormFactory } from '@features/recipes/edit-recipe/edit-recipe.factory';
import {
  COOKING_METHODS,
  MEAL_TYPES,
  CUISINE_TYPES,
  DIET_TYPES,
  COURSE_TYPES,
  VISIBILITY_TYPES,
  Recipe,
} from '@features/recipes/recipe.types';
import { ErrorService } from '@core/errors/error.service';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTabsModule } from '@angular/material/tabs';
import { CheckboxGroupComponent } from '@features/recipes/components/checkbox-group/checkbox-group.component';
import { ApiError } from '@core/models/api-error.model';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';
import { ImageService } from '@core/services/image.service';
import {
  ImageSelectorComponent,
  ImageSelectorExistingImage,
  ImageSelectorRemovedImage,
} from '@shared/ui/image-selector/image-selector.component';
import {
  MatInputNumberComponent,
  MatInputSelectComponent,
  MatInputTextareaComponent,
  MatInputTextComponent,
} from '@ng-modular-forms/material';

import { RecipeNotFoundComponent } from '../components/recipe-not-found/recipe-not-found.component';
import { redirectTo } from '@shared/utils/redirect-to';
import { DialogService } from '@shared/ui/dialog/dialog.service';
import { logDebug, logError } from '@shared/utils/logging';
import { AuthenticationService } from '@core/services/authentication.service';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';

@Component({
  selector: 'app-edit-recipe',
  imports: [
    FluidContainerComponent,
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule,
    MatTabsModule,
    MatExpansionModule,
    CdkDropList,
    CdkDrag,
    CdkDragHandle,
    MatInputTextComponent,
    MatInputTextareaComponent,
    MatInputNumberComponent,
    MatInputSelectComponent,
    ButtonComponent,
    TranslatePipe,
    CheckboxGroupComponent,
    FullPageLoaderComponent,
    ImageSelectorComponent,
    RecipeNotFoundComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './edit-recipe.component.html',
  styleUrls: ['./edit-recipe.component.css'],
})
export class EditRecipeComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly snackbar = inject(SnackbarService);
  private readonly authService = inject(AuthenticationService);
  private readonly dialogService = inject(DialogService);
  private readonly translate = inject(TranslateService);
  private readonly mediaService = inject(MediaService);
  private readonly recipeService = inject(RecipeService);
  private readonly errorService = inject(ErrorService);
  private readonly imageService = inject(ImageService);

  private _form = signal<RecipeForm>(RecipeFormFactory.recipe());
  private _loading = signal<boolean>(true);
  private _validated = signal<boolean>(false);
  private _status = signal<'idle' | 'submitting' | 'error'>('idle');
  private _loadedRecipe = signal<Recipe | null>(null);
  private _activeVariantIndex = signal<number | null>(0);

  readonly form = this._form.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly validated = this._validated.asReadonly();
  readonly status = this._status.asReadonly();
  readonly loadedRecipe = this._loadedRecipe.asReadonly();
  readonly activeVariantIndex = this._activeVariantIndex.asReadonly();

  constructor() {
    const recipeId = this.route.snapshot.paramMap.get('id');
    const state = this.router.currentNavigation()?.extras.state as { recipe?: Recipe } | undefined;

    if (state?.recipe) {
      logDebug('Using provided recipe from route state:', state);
      this._loadedRecipe.set(state.recipe);
      this._form.set(RecipeFormFactory.recipe(state.recipe));
      this._loading.set(false);
      return;
    }

    if (recipeId && recipeId !== 'new') {
      this.loadRecipe(recipeId);
    } else {
      this._loading.set(false);
    }
  }

  isMobile = this.mediaService.isMobile;

  MAX_VARIANTS = COOKING_METHODS.length;

  cookingMethodOptions = COOKING_METHODS.map((method) => ({
    value: method,
    label: this.translate.instant(
      `recipes.labels.cookingMethods.${method.toLowerCase().replaceAll('_', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  courseOptions = COURSE_TYPES.map((course) => ({
    value: course,
    label: this.translate.instant(
      `recipes.attributes.courses.${course.toLowerCase().replaceAll('_', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  cuisineOptions = CUISINE_TYPES.map((cuisine) => ({
    value: cuisine,
    label: this.translate.instant(
      `recipes.attributes.cuisines.${cuisine.toLowerCase().replaceAll('_', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  dietTypeOptions = DIET_TYPES.map((dietType) => ({
    value: dietType,
    label: this.translate.instant(
      `recipes.attributes.dietTypes.${dietType.toLowerCase().replaceAll('_', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  mealTypeOptions = MEAL_TYPES.map((mealType) => ({
    value: mealType,
    label: this.translate.instant(
      `recipes.attributes.mealTypes.${mealType.toLowerCase().replaceAll('_', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  visibilityOptions = VISIBILITY_TYPES.map((visibility) => ({
    value: visibility,
    label: this.translate.instant(`recipes.visibility.${visibility.toLowerCase()}`),
  })).sort((a, b) => a.label.localeCompare(b.label));

  get variants(): FormArray<RecipeVariantForm> {
    return this._form().get('variants') as FormArray<RecipeVariantForm>;
  }

  getVariantLabel(index: number) {
    const variant = this.variants.at(index);
    const method = variant.get('cookingMethod')?.value;
    return method
      ? this.cookingMethodOptions.find((option) => option.value === method)?.label
      : `recipes.edit.noCookingMethodSelected`;
  }

  promptConfirmDeleteVariant(index: number) {
    const cookingMethod = this.variants.at(index).get('cookingMethod')?.value;
    const name = this.variants.at(index).get('name')?.value;
    if (cookingMethod === null && (name == null || name.length === 0)) {
      this.removeVariant(index);
      return;
    }
    // Bug: when there is only 1 variant and the dialog shows to delete it, it does not trigger change detection.
    const dialog = this.dialogService.openConfirmationDialog(
      'recipes.edit.deleteVariantConfirmation.title',
      'recipes.edit.deleteVariantConfirmation.message',
    );

    dialog.afterClosed().subscribe((response) => {
      if (response) {
        this.removeVariant(index);
      }
    });
  }

  setActiveVariantIndex(index: number) {
    this._activeVariantIndex.set(index);
  }

  removeVariant(index: number) {
    this.variants.removeAt(index);
    this._activeVariantIndex.set(this.variants.length - 1);
  }

  addVariant() {
    if (this.variants.length >= this.MAX_VARIANTS) {
      return;
    }

    this.variants.push(RecipeFormFactory.variant());
    this._activeVariantIndex.set(this.variants.length - 1);
  }

  removeExistingImage(index: number) {
    const control = this._form().controls.imageIds;
    control.setValue(control.value.filter((_, i) => i !== index));
  }

  removeImage(index: number) {
    const control = this._form().controls.images;
    control.setValue((control.value ?? []).filter((_, imageIndex) => imageIndex !== index));
  }

  getExistingRecipeImages(): ImageSelectorExistingImage[] {
    return this._form().controls.imageIds.value.map((id, index) => ({
      id,
      src: this.imageService.getImageUrl(id),
      alt: `Recipe image ${index + 1}`,
    }));
  }

  onRecipeImagesSelected(images: File[]): void {
    const control = this._form().controls.images;
    control.setValue([...(control.value ?? []), ...images]);
  }

  onRecipeImageRemoved(image: ImageSelectorRemovedImage): void {
    if (image.type === 'existing') {
      this.removeExistingImage(image.index);
      return;
    }

    this.removeImage(image.index);
  }

  submit() {
    this._validated.set(true);

    if (this._form().invalid) {
      this._form().markAllAsTouched();
      logError('Form is invalid, cannot submit:', this._form());
      return;
    }

    this._status.set('submitting');
    this._form().disable();

    const request = this._form().getRawValue();

    if (request.images?.length) {
      this.imageService.uploadImages(request.images).subscribe({
        next: (uploadedImageIds) => {
          request.imageIds = [...(request.imageIds ?? []), ...uploadedImageIds];
          this.saveRecipe(request);
        },
        error: (error: ApiError) => {
          logError('Error uploading images:', error);
          this._form().enable();
          this._status.set('error');
        },
      });
    } else {
      this.saveRecipe(request);
    }
  }

  dropVariant(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.variants.controls, event.previousIndex, event.currentIndex);
    this._activeVariantIndex.set(event.currentIndex);
  }

  redirectToNew() {
    redirectTo(this.router, '/app/recipes/edit/new');
  }

  goBack() {
    if (this.loadedRecipe()) {
      this.router.navigate(['/app/recipes/details', this.loadedRecipe()!.id], {
        state: { recipe: this.loadedRecipe() },
      });
    } else {
      this.router.navigate(['/app/recipes/list']);
    }
  }

  private loadRecipe(recipeId: string) {
    this.recipeService.loadRecipe(recipeId!).subscribe({
      next: (recipe) => {
        logDebug('Loaded recipe:', recipe);

        const currentSubject = this.authService.authUser()?.identityProviderSubject;
        if (recipe.authorSubject !== currentSubject) {
          logDebug('Current user is not the author of this recipe, redirecting to details page.');
          this.router.navigate([`/app/recipes/details/${recipe.id}`], { state: { recipe } });
          setTimeout(() => this.snackbar.openSnackBar(SnackbarType.ERROR, 'errors.notAuthorized'));
          return;
        }

        this._loadedRecipe.set(recipe);
        this._form.set(RecipeFormFactory.recipe(recipe));
        this._loading.set(false);
      },
      error: (error: ApiError) => {
        logDebug('Error loading recipe:', error);
        this._status.set('error');
      },
    });
  }

  private saveRecipe(recipeData: RecipeFormModel) {
    this.recipeService.saveRecipe(recipeData, this._loadedRecipe()!.id).subscribe({
      next: (recipe) => {
        logDebug('Recipe saved successfully:', recipe);
        this.navigateToRecipe(recipe);
      },
      error: (error: ApiError) => {
        logError('Error publishing recipe:', error);

        this._form().enable();

        if (error.hasFormError()) {
          logDebug('Has a form error, so going idle..');
          this._status.set('idle');
          this.errorService.populateFormErrors(this._form(), error);
          return;
        }

        this._status.set('error');
      },
    });
  }

  private navigateToRecipe(recipe: Recipe) {
    const recipeUrl = `/app/recipes/details/${recipe.id}`;
    this.router.navigate([recipeUrl], { state: { recipe } });
  }
}
