package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.recipe.dto.RecipeRequest;
import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeDetailsDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeVariantRequest;
import com.ronbodnar.recipes.recipe.domain.CookingMethod;

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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTests {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    public void returnsAllRecipes() {
        given(recipeService.getAllSummaries(0, 10, null))
                .willReturn(new PageImpl<>(List.of(RecipeSummaryDTO.fromEntity(new Recipe(), List.of()))));

        Page<RecipeSummaryDTO> allRecipes = recipeService.getAllSummaries(0, 10, null);

        assertEquals(1, allRecipes.getContent().size());
    }

    @Test
    public void returnsPaginatedRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Recipe recipe = new Recipe();
            recipe.setId(UUID.randomUUID());

            recipes.add(recipe);
        }

        Page<Recipe> page = new PageImpl<>(
                recipes,
                PageRequest.of(0, 10),
                11
        );

        given(recipeRepository.findAll(any(Pageable.class))).willReturn(page);

        Page<RecipeSummaryDTO> result = recipeService.getAllSummaries(0, 10, null);

        assertEquals(10, result.getContent().size());
        assertEquals(11, result.getTotalElements());
    }

    @Test
    public void returnsRecipeById() {
        UUID id = UUID.randomUUID();

        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setTitle("Test recipe");
        recipe.setAuthorId(UUID.randomUUID());

        given(recipeRepository.findById(id)).willReturn(Optional.of(recipe));

        assertEquals(RecipeDetailsDTO.fromRecipe(recipe, List.of()), recipeService.getById(id));
    }

    @Test
    public void handleCreateRequest_returnsCreatedRecipe() {
        // Arrange
        RecipeVariantRequest variant = new RecipeVariantRequest(
                "Oven",
                0,
                0,
                0,
                CookingMethod.OVEN,
                List.of(),
                List.of(),
                List.of()
        );

        RecipeRequest request = new RecipeRequest(
                UUID.randomUUID(),
                "Test recipe",
                "Test description",
                List.of(variant)
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

        // Act
        RecipeDetailsDTO createdRecipe = recipeService.handleCreateRequest(request, Optional.empty(), UUID.randomUUID());


        // Assert
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

        verify(recipeRepository).save(recipeCaptor.capture());

        Recipe savedRecipe = recipeCaptor.getValue();

        assertEquals(generatedId, savedRecipe.getId());
        assertEquals(generatedId, createdRecipe.id());
        assertEquals("Test title", createdRecipe.title());
        assertEquals("Test description", createdRecipe.description());
    }

    @Test
    public void handleCreateRequest_whenImageExtractionFails_throwsImageExtractionException() throws IOException {
        // Arrange
        MultipartFile multipartFile = mock(MultipartFile.class);

        RecipeRequest request = new RecipeRequest(
                UUID.randomUUID(),
                "Test recipe",
                "Test description",
                List.of()
        );

        given(multipartFile.getBytes()).willThrow(new IOException("Failed to read image"));

        assertThrows(BusinessException.class, () -> recipeService.handleCreateRequest(request, Optional.of(List.of(multipartFile)), UUID.randomUUID()));
        verify(recipeRepository, times(0)).save(any(Recipe.class));
    }
}
