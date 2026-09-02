import { inject, Injectable } from '@angular/core';
import { from, Observable, switchMap } from 'rxjs';
import { FetchApiService } from '@core/services/fetch-api.service';
import { RecipeFormModel, RecipeFormRequest } from './edit-recipe/edit-recipe.types';
import { toBackendEnum } from '@shared/utils/enum-mapping.utils';
import { Recipe, RecipeSummary } from './recipe.types';
import { resizeImage } from '@shared/utils/resize-image';
import { PaginatedResponse } from '@core/interfaces/paginated-response.interface';

@Injectable({
  providedIn: 'root',
})
export class RecipeService {
  private fetchApi = inject(FetchApiService);

  loadPaginatedRecipes(
    page: number,
    pageSize: number,
    sortBy: string = 'id=desc',
  ): Observable<PaginatedResponse<RecipeSummary>> {
    return this.fetchApi.fetchPaginatedData<RecipeSummary>('recipes', {
      method: 'GET',
      parameters: {
        paginationStart: page,
        paginationLength: pageSize,
        paginationSortOrder: sortBy,
      },
    });
  }

  loadRecipe(recipeId: string): Observable<Recipe> {
    return this.fetchApi.getData<Recipe>(`recipes/${recipeId}`);
  }

  saveRecipe(recipeData: RecipeFormModel, recipeId: string | null): Observable<Recipe> {
    const resizedImages = Promise.all((recipeData.images ?? []).map((image) => resizeImage(image)));
    return from(resizedImages).pipe(
      switchMap((images) => {
        console.log('Raw recipeData:', recipeData);
        const formData = new FormData();

        for (const image of images) {
          formData.append('images', image);
        }

        const recipe: RecipeFormRequest = {
          id: recipeData.id,
          title: recipeData.title,
          description: recipeData.description,
          imageIds: recipeData.imageIds,
          cuisines: recipeData.cuisines.map((cuisine) => toBackendEnum(cuisine) ?? ''),
          courses: recipeData.courses.map((course) => toBackendEnum(course) ?? ''),
          mealTypes: recipeData.mealTypes.map((mealType) => toBackendEnum(mealType) ?? ''),
          dietTypes: recipeData.dietTypes.map((dietType) => toBackendEnum(dietType) ?? ''),
          variants: recipeData.variants.map((variant) => ({
            name: variant.name ?? null,
            prepTime: variant.prepTime ?? 0,
            cookTime: variant.cookTime ?? 0,
            servings: variant.servings ?? 0,
            cookingMethod: toBackendEnum(variant.cookingMethod) ?? null,
            ingredients: variant.ingredients === '' ? [] : variant.ingredients.split('\n'),
            instructions: variant.instructions === '' ? [] : variant.instructions.split('\n'),
            notes: variant.notes === '' ? [] : variant.notes.split('\n'),
          })),
        };
        console.log('Recipe data to be sent:', recipe);

        formData.append('recipe', new Blob([JSON.stringify(recipe)], { type: 'application/json' }));

        return this.fetchApi.fetch<Recipe>(`recipes${recipeId ? `/${recipeId}` : ''}`, {
          method: recipeId ? 'PUT' : 'POST',
          requestBody: formData,
        });
      }),
    );
  }

  deleteRecipe(recipeId: string): Observable<void> {
    return this.fetchApi.fetch<void>(`recipes/${recipeId}`, {
      method: 'DELETE',
    });
  }
}
