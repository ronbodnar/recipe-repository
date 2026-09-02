import { FormGroup, FormControl, FormArray } from '@angular/forms';
import {
  MealType,
  Cuisine,
  DietType,
  Course,
  COOKING_METHODS,
  RecipeVariant,
  Recipe,
} from '@features/recipes/recipe.types';
import { Validators } from '@angular/forms';
import { RecipeForm } from '@features/recipes/edit-recipe/edit-recipe.types';

export class RecipeFormFactory {
  static recipe(v?: Partial<Recipe>): RecipeForm {
    return new FormGroup({
      id: new FormControl<string | null>(v?.id ?? null),
      title: new FormControl(v?.title ?? '', {
        validators: [Validators.required],
        nonNullable: true,
      }),
      description: new FormControl(v?.description ?? '', { nonNullable: true }),
      imageIds: new FormControl<string[]>(v?.images ?? [], { nonNullable: true }),
      images: new FormControl<File[] | null>(null, {
        validators: [Validators.maxLength(2)],
        nonNullable: true,
      }),
      courses: new FormControl<Course[]>(v?.courses ?? [], { nonNullable: true }),
      cuisines: new FormControl<Cuisine[]>(v?.cuisines ?? [], { nonNullable: true }),
      mealTypes: new FormControl<MealType[]>(v?.mealTypes ?? [], { nonNullable: true }),
      dietTypes: new FormControl<DietType[]>(v?.dietTypes ?? [], { nonNullable: true }),
      variants: new FormArray<FormGroup>(
        (v?.variants ?? [{}]).map((variant) => this.variant(variant)),
        {
          validators: [Validators.minLength(1), Validators.maxLength(COOKING_METHODS.length)],
        },
      ),
    });
  }

  static variant(v?: Partial<RecipeVariant>) {
    return new FormGroup({
      id: new FormControl(v?.id ?? null),
      name: new FormControl(v?.name ?? null),
      cookingMethod: new FormControl(v?.cookingMethod ?? null, {
        validators: [Validators.required],
      }),
      prepTime: new FormControl(v?.prepTime ?? null),
      cookTime: new FormControl(v?.cookTime ?? null),
      servings: new FormControl(v?.servings ?? null),
      ingredients: new FormControl(v?.ingredients?.join('\n') ?? '', { nonNullable: true }),
      instructions: new FormControl(v?.instructions?.join('\n') ?? '', { nonNullable: true }),
      notes: new FormControl(v?.notes?.join('\n') ?? '', { nonNullable: true }),
    });
  }
}
