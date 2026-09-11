package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.modules.recipe.Recipe;
import com.ronbodnar.recipes.modules.recipe.RecipeRepository;
import com.ronbodnar.recipes.modules.recipe.RecipeService;
import com.ronbodnar.recipes.modules.recipe.domain.RecipeVisibility;
import com.ronbodnar.recipes.modules.recipe.dto.RecipeRequest;
import com.ronbodnar.recipes.modules.recipe.dto.RecipeSummaryDTO;
import com.ronbodnar.recipes.modules.recipe.dto.RecipeDetailsDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTests {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void getAllSummaries_returnsRecipeSummaries() {
        Long authorId = 1L;

        Recipe recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.setAuthorId(authorId);

        given(recipeRepository.findAllByAuthorWithImages(
                eq(authorId),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(List.of(recipe)));

        Page<RecipeSummaryDTO> allRecipes =
                recipeService.getAllSummaries(authorId, PageRequest.of(0, 10));

        assertEquals(1, allRecipes.getContent().size());
    }

    @Test
    void getAllSummaries_returnsPaginatedRecipes() {
        Long authorId = 1L;

        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Recipe recipe = new Recipe();
            recipe.setId(UUID.randomUUID());
            recipe.setAuthorId(authorId);

            recipes.add(recipe);
        }

        Page<Recipe> page = new PageImpl<>(
                recipes,
                PageRequest.of(0, 10),
                11
        );

        given(recipeRepository.findAllByAuthorWithImages(
                eq(authorId),
                any(Pageable.class)
        )).willReturn(page);

        Page<RecipeSummaryDTO> result = recipeService.getAllSummaries(authorId, PageRequest.of(0, 10));

        assertEquals(10, result.getContent().size());
        assertEquals(11, result.getTotalElements());
    }

    @Test
    void getById_returnsRecipeDetails() {
        UUID id = UUID.randomUUID();

        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setTitle("Test recipe");
        recipe.setAuthorId(1L);

        given(recipeRepository.findById(id)).willReturn(Optional.of(recipe));

        RecipeDetailsDTO detailsDTO = RecipeDetailsDTO.from(recipe);

        assertEquals(detailsDTO, recipeService.getById(id));
    }

    @Test
    void handleCreateRequest_returnsCreatedRecipe() {
        RecipeRequest request = new RecipeRequest(
                UUID.randomUUID(),
                "Test recipe",
                "Test description",
                RecipeVisibility.GROUP,
                List.of()
        );

        UUID generatedId = UUID.randomUUID();

        given(recipeRepository.save(any(Recipe.class)))
                .willAnswer(
                    invocation -> {
                        Recipe recipe = invocation.getArgument(0);
                        recipe.setId(generatedId);
                        return recipe;
                    }
                );

        RecipeDetailsDTO createdRecipe = recipeService.handleCreateRequest(request, 1L);

        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

        then(recipeRepository).should().save(recipeCaptor.capture());

        Recipe savedRecipe = recipeCaptor.getValue();

        assertEquals(generatedId, savedRecipe.getId());
        assertEquals(generatedId, createdRecipe.id());
        assertEquals("Test recipe", createdRecipe.title());
        assertEquals("Test description", createdRecipe.description());
    }
}
