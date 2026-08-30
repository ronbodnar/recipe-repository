package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.recipe.dto.RecipeRequest;
import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeDetailsDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeVariantRequest;
import com.ronbodnar.recipes.recipe.domain.CookingMethod;

import com.ronbodnar.recipes.recipe.image.RecipeImageService;
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTests {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeImageService recipeImageService;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void getAllSummaries_returnsRecipeSummaries() {
        UUID authorId = UUID.randomUUID();

        Recipe recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.setAuthorId(authorId);

        given(recipeRepository.findAllByAuthorId(
                eq(authorId),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(List.of(recipe)));

        given(recipeImageService.findByRecipeIdIn(
                List.of(recipe.getId())
        )).willReturn(List.of());

        Page<RecipeSummaryDTO> allRecipes =
                recipeService.getAllSummaries(authorId, 0, 10, null);

        assertEquals(1, allRecipes.getContent().size());
    }

    @Test
    void getAllSummaries_returnsPaginatedRecipes() {
        UUID authorId = UUID.randomUUID();

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

        given(recipeRepository.findAllByAuthorId(
                eq(authorId),
                any(Pageable.class)
        )).willReturn(page);

        given(recipeImageService.findByRecipeIdIn(
                anyList()
        )).willReturn(List.of());

        Page<RecipeSummaryDTO> result = recipeService.getAllSummaries(authorId, 0, 10, null);

        assertEquals(10, result.getContent().size());
        assertEquals(11, result.getTotalElements());
    }

    @Test
    void getById_returnsRecipeDetails() {
        UUID id = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setTitle("Test recipe");
        recipe.setAuthorId(authorId);

        given(recipeRepository.findById(id)).willReturn(Optional.of(recipe));

        RecipeDetailsDTO detailsDTO = RecipeDetailsDTO.fromRecipe(recipe, List.of());

        assertEquals(detailsDTO, recipeService.getById(id));
    }

    @Test
    void handleCreateRequest_returnsCreatedRecipe() {
        RecipeRequest request = new RecipeRequest(
                UUID.randomUUID(),
                "Test recipe",
                "Test description",
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

        RecipeDetailsDTO createdRecipe = recipeService.handleCreateRequest(request, Optional.empty(), UUID.randomUUID());

        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

        then(recipeRepository).should().save(recipeCaptor.capture());

        Recipe savedRecipe = recipeCaptor.getValue();

        assertEquals(generatedId, savedRecipe.getId());
        assertEquals(generatedId, createdRecipe.id());
        assertEquals("Test recipe", createdRecipe.title());
        assertEquals("Test description", createdRecipe.description());
    }

    @Test
    void handleCreateRequest_whenImageCreationFails_propagatesException() {
        RecipeRequest request = new RecipeRequest(
                UUID.randomUUID(),
                "Test recipe",
                "Test description",
                List.of()
        );

        UUID authorId = UUID.randomUUID();
        UUID recipeId = UUID.randomUUID();

        MultipartFile image = mock(MultipartFile.class);

        given(recipeRepository.existsByTitle(request.title()))
                .willReturn(false);

        given(recipeRepository.save(any(Recipe.class)))
                .willAnswer(invocation -> {
                    Recipe recipe = invocation.getArgument(0);
                    recipe.setId(recipeId);
                    return recipe;
                });

        BusinessException exception =
                new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);

        given(recipeImageService.createImages(recipeId, List.of(image)))
                .willThrow(exception);

        assertThrows(
                BusinessException.class,
                () -> recipeService.handleCreateRequest(
                        request,
                        Optional.of(List.of(image)),
                        authorId
                )
        );

        then(recipeRepository).should().save(any(Recipe.class));

        then(recipeImageService).should()
                .createImages(recipeId, List.of(image));
    }
}
