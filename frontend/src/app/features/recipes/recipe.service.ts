import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FetchApiService } from '@core/services/fetch-api.service';
import { RecipeFormModel, RecipeFormRequest } from './edit-recipe/edit-recipe.types';
import { Recipe, RecipeSummary } from './recipe.types';
import { PaginatedResponse } from '@core/interfaces/paginated-response.interface';
import { logDebug } from '@shared/utils/logging';

@Injectable({
  providedIn: 'root',
})
export class RecipeService {
  private fetchApi = inject(FetchApiService);

  loadPaginatedRecipes(
    page: number,
    size: number,
    sort: string[] = ['id,desc'],
  ): Observable<PaginatedResponse<RecipeSummary>> {
    return this.fetchApi.fetchPaginatedData<RecipeSummary>('recipes', {
      method: 'GET',
      parameters: {
        page,
        size,
        sort,
      },
    });
  }

  loadDiscoverRecipes(
    page: number,
    size: number,
    sort: string[] = ['id,desc'],
  ): Observable<PaginatedResponse<RecipeSummary>> {
    return this.fetchApi.fetchPaginatedData<RecipeSummary>('recipes/discover', {
      method: 'GET',
      parameters: {
        page,
        size,
        sort,
      },
    });
  }

  loadRecipe(recipeId: string): Observable<Recipe> {
    return this.fetchApi.getData<Recipe>(`recipes/${recipeId}`);
  }

  saveRecipe(recipeData: RecipeFormModel, recipeId: string | null): Observable<Recipe> {
    const recipe: RecipeFormRequest = {
      id: recipeData.id,
      title: recipeData.title,
      description: recipeData.description,
      source: recipeData.source ?? null,
      visibility: recipeData.visibility,
      imageIds: recipeData.imageIds,
      cuisines: recipeData.cuisines,
      courses: recipeData.courses,
      mealTypes: recipeData.mealTypes,
      dietTypes: recipeData.dietTypes,
      variants: recipeData.variants.map((variant) => ({
        name: variant.name ?? null,
        prepTime: variant.prepTime ?? 0,
        cookTime: variant.cookTime ?? 0,
        yield: variant.yield ?? null,
        cookingMethod: variant.cookingMethod ?? null,
        ingredients: variant.ingredients === '' ? [] : variant.ingredients.split('\n'),
        instructions: variant.instructions === '' ? [] : variant.instructions.split('\n'),
        notes: variant.notes === '' ? [] : variant.notes.split('\n'),
      })),
    };
    logDebug('Recipe data to be sent:', recipe);

    return this.fetchApi.fetch<Recipe>(`recipes${recipeId ? `/${recipeId}` : ''}`, {
      method: recipeId ? 'PUT' : 'POST',
      requestBody: recipe,
    });
  }

  deleteRecipe(recipeId: string): Observable<void> {
    return this.fetchApi.deleteData<void>(`recipes/${recipeId}`);
  }
}
