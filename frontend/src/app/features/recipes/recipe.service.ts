import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FetchApiService } from '@core/services/fetch-api.service';
import { RecipeFormModel, RecipeFormRequest } from './edit-recipe/edit-recipe.types';
import { Recipe, RecipeSummary } from './recipe.types';
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
    const recipe: RecipeFormRequest = {
      id: recipeData.id,
      title: recipeData.title,
      description: recipeData.description,
      visibility: recipeData.visibility ?? 'FAMILY',
      imageIds: recipeData.imageIds,
      cuisines: recipeData.cuisines,
      courses: recipeData.courses,
      mealTypes: recipeData.mealTypes,
      dietTypes: recipeData.dietTypes,
      variants: recipeData.variants.map((variant) => ({
        name: variant.name ?? null,
        prepTime: variant.prepTime ?? 0,
        cookTime: variant.cookTime ?? 0,
        servings: variant.servings ?? 0,
        cookingMethod: variant.cookingMethod ?? null,
        ingredients: variant.ingredients === '' ? [] : variant.ingredients.split('\n'),
        instructions: variant.instructions === '' ? [] : variant.instructions.split('\n'),
        notes: variant.notes === '' ? [] : variant.notes.split('\n'),
      })),
    };
    console.log('Recipe data to be sent:', recipe);

    return this.fetchApi.fetch<Recipe>(`recipes${recipeId ? `/${recipeId}` : ''}`, {
      method: recipeId ? 'PUT' : 'POST',
      requestBody: recipe,
    });
  }

  deleteRecipe(recipeId: string): Observable<void> {
    return this.fetchApi.deleteData<void>(`recipes/${recipeId}`);
  }
}
