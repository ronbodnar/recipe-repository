import { FormGroup, FormControl, FormArray } from '@angular/forms';
import {
  Cuisine,
  MealType,
  Course,
  DietType,
  CookingMethod,
  RecipeVisibility,
} from '@features/recipes/recipe.types';

export type RecipeForm = FormGroup<{
  id: FormControl<string | null>;
  title: FormControl<string>;
  description: FormControl<string>;
  visibility: FormControl<RecipeVisibility>;
  imageIds: FormControl<string[]>; // Images from the backend (existing images)
  images: FormControl<File[] | null>;
  cuisines: FormControl<Cuisine[]>;
  mealTypes: FormControl<MealType[]>;
  courses: FormControl<Course[]>;
  dietTypes: FormControl<DietType[]>;
  variants: FormArray<RecipeVariantForm>;
}>;

export type RecipeVariantForm = FormGroup<{
  id: FormControl<string | null>;
  name: FormControl<string | null>;
  cookingMethod: FormControl<CookingMethod | null>;
  prepTime: FormControl<number | null>;
  cookTime: FormControl<number | null>;
  servings: FormControl<number | null>;
  ingredients: FormControl<string>;
  instructions: FormControl<string>;
  notes: FormControl<string>;
}>;

export type RecipeFormModel = {
  id: string | null;
  title: string;
  description: string;
  visibility: RecipeVisibility;
  imageIds?: string[];
  images: File[] | null;
  cuisines: Cuisine[];
  mealTypes: MealType[];
  courses: Course[];
  dietTypes: DietType[];
  variants: RecipeVariantModel[];
};

export type RecipeVariantModel = {
  id: string | null;
  name: string | null;
  prepTime: number | null;
  cookTime: number | null;
  servings: number | null;
  cookingMethod: CookingMethod | null;
  ingredients: string;
  instructions: string;
  notes: string;
};

export type RecipeFormRequest = {
  id: string | null;
  title: string;
  description: string;
  visibility: RecipeVisibility;
  imageIds?: string[];
  cuisines: Cuisine[];
  mealTypes: MealType[];
  courses: Course[];
  dietTypes: DietType[];
  variants: RecipeVariantRequest[];
};

export type RecipeVariantRequest = {
  name: string | null;
  prepTime: number;
  cookTime: number;
  servings: number;
  cookingMethod: CookingMethod | null;
  ingredients: string[];
  instructions: string[];
  notes: string[];
};
