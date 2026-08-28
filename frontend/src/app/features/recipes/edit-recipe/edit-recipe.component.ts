import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormArray, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MediaService } from '@core/services/media.service';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { RecipeForm, RecipeVariantForm } from './edit-recipe.types';
import { RecipeService } from '../recipe.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@shared/pipes/translate.pipe';
import { RecipeFormFactory } from '@features/recipes/edit-recipe/edit-recipe.factory';
import {
  COOKING_METHODS,
  MEAL_TYPES,
  CUISINE_TYPES,
  DIET_TYPES,
  COURSE_TYPES,
} from '@features/recipes/recipe.types';
import { ErrorService } from '@core/errors/error.service';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTabsModule } from '@angular/material/tabs';
import { CheckboxGroupComponent } from '@features/recipes/components/checkbox-group/checkbox-group.component';
import { toBackendEnum } from '@shared/utils/enum-mapping.utils';
import {
  InputNumberComponent,
  InputSelectComponent,
  InputTextareaComponent,
  InputTextComponent,
  InputFileSelectorComponent,
} from '@ng-modular-forms/core';
import { FileUrlPipe } from '@shared/pipes/file-url.pipe';
import { ApiError } from '@core/models/api-error.model';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';
import { TranslateService } from '@core/services/translate.service';
import { ImageService } from '@core/services/image.service';

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
    InputTextComponent,
    InputTextareaComponent,
    InputNumberComponent,
    InputSelectComponent,
    InputFileSelectorComponent,
    ButtonComponent,
    TranslatePipe,
    MatExpansionModule,
    CheckboxGroupComponent,
    FullPageLoaderComponent,
    FileUrlPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './edit-recipe.component.html',
  styleUrls: ['./edit-recipe.component.css'],
})
export class EditRecipeComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly translate = inject(TranslateService);
  private readonly mediaService = inject(MediaService);
  private readonly recipeService = inject(RecipeService);
  private readonly errorService = inject(ErrorService);
  readonly imageService = inject(ImageService);

  form = signal<RecipeForm>(RecipeFormFactory.recipe());
  loading = signal<boolean>(true);
  validated = signal<boolean>(false);
  status = signal<'idle' | 'submitting' | 'error'>('idle');
  loadedRecipeId = signal<string | null>(null);
  activeVariantIndex = signal<number>(0);

  constructor() {
    const recipeId = this.route.snapshot.paramMap.get('id');
    if (recipeId && recipeId !== 'new') {
      this.loadRecipe(recipeId);
    } else {
      this.loading.set(false);
    }
  }

  isMobile = this.mediaService.isMobile;

  MAX_VARIANTS = COOKING_METHODS.length;

  cookingMethodOptions = COOKING_METHODS.map((method) => ({
    value: toBackendEnum(method),
    label: this.translate.get(
      `recipes.labels.cookingMethods.${method.toLowerCase().replaceAll(' ', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  courseOptions = COURSE_TYPES.map((course) => ({
    value: toBackendEnum(course),
    label: this.translate.get(
      `recipes.categories.courses.${course.toLowerCase().replaceAll(' ', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  cuisineOptions = CUISINE_TYPES.map((cuisine) => ({
    value: toBackendEnum(cuisine),
    label: this.translate.get(
      `recipes.categories.cuisines.${cuisine.toLowerCase().replaceAll(' ', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  dietTypeOptions = DIET_TYPES.map((dietType) => ({
    value: toBackendEnum(dietType),
    label: this.translate.get(
      `recipes.categories.dietTypes.${dietType.toLowerCase().replaceAll(' ', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  mealTypeOptions = MEAL_TYPES.map((mealType) => ({
    value: toBackendEnum(mealType),
    label: this.translate.get(
      `recipes.categories.mealTypes.${mealType.toLowerCase().replaceAll(' ', '-')}`,
    ),
  })).sort((a, b) => a.label.localeCompare(b.label));

  get variants(): FormArray<RecipeVariantForm> {
    return this.form().get('variants') as FormArray<RecipeVariantForm>;
  }

  getVariantLabel(index: number) {
    const variant = this.variants.at(index);
    const method = variant.get('cookingMethod')?.value;
    return method
      ? this.cookingMethodOptions.find((option) => option.value === method)?.label
      : `recipes.edit.noCookingMethodSelected`;
  }

  removeVariant(index: number) {
    this.variants.removeAt(index);
    this.activeVariantIndex.set(0);
  }

  addVariant() {
    if (this.variants.length >= this.MAX_VARIANTS) {
      return;
    }

    this.variants.push(RecipeFormFactory.variant());
    this.activeVariantIndex.set(this.variants.length - 1);
  }

  removeExistingImage(index: number) {
    const control = this.form().controls.existingImages;

    control.setValue(control.value.filter((_, i) => i !== index));
  }

  removeImage(index: number) {
    const imagesControl = this.form().get('images') as FormControl<File[] | null>;
    if (!imagesControl) {
      return;
    }
    const currentImages = imagesControl.value ?? [];
    const updatedImages = currentImages.filter((_, i) => i !== index);
    imagesControl.setValue(updatedImages);
  }

  loadRecipe(recipeId: string) {
    this.loadedRecipeId.set(recipeId);
    this.recipeService.loadRecipe(recipeId!).subscribe({
      next: (recipe) => {
        console.log('Loaded recipe:', recipe);
        this.form.set(RecipeFormFactory.recipe(recipe));
        this.loading.set(false);
      },
      error: (error: ApiError) => {
        console.error('Error loading recipe:', error);
        this.status.set('error');
        this.loading.set(false);
      },
    });
  }

  submit() {
    this.validated.set(true);

    if (this.form().invalid) {
      this.form().markAllAsTouched();
      return;
    }

    this.status.set('submitting');
    this.form().disable();

    const request = this.form().getRawValue();

    this.recipeService.saveRecipe(request, this.loadedRecipeId()).subscribe({
      next: (recipe) => {
        console.log('Recipe saved successfully:', recipe);
        this.navigateToRecipe(recipe.id);
      },
      error: (error: ApiError) => {
        console.error('Error publishing recipe:', error);

        this.form().enable();

        if (error.hasFormError()) {
          console.log('Has a form error, so going idle..');
          this.status.set('idle');
          this.errorService.populateFormErrors(this.form(), error);
          return;
        }

        this.status.set('error');
      },
    });
  }

  navigateToRecipe(recipeId: string) {
    const recipeUrl = `/app/recipes/details/${recipeId}`;
    this.router.navigate([recipeUrl]);
  }
}
