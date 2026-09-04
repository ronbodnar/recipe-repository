package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.image.ImageServiceClient;
import com.ronbodnar.recipes.recipe.dto.RecipeDetailsDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeRequest;
import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final ImageServiceClient imageServiceClient;

    public RecipeService(RecipeRepository recipeRepository, ImageServiceClient imageServiceClient) {
        this.recipeRepository = recipeRepository;
        this.imageServiceClient = imageServiceClient;
    }

    public Page<RecipeSummaryDTO> getAllSummaries(
            String authorId,
            int paginationStart,
            int paginationLength,
            String paginationSortOrder
    ) {
        Page<Recipe> recipes = recipeRepository.findAllByAuthorKeyWithImages(
                authorId,
                PageRequest.of(paginationStart, paginationLength)
        );

        return recipes.map(recipe ->
                new RecipeSummaryDTO(
                        recipe.getId(),
                        recipe.getTitle(),
                        recipe.getDescription(),
                        recipe.getImageIds()
                )
        );
    }

    @Transactional(readOnly = true)
    public RecipeDetailsDTO getById(UUID id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() ->
                new BusinessException(
                        ErrorCode.RECIPE_NOT_FOUND,
                        "Did not find any recipe details with ID %s! Ensure the ID is correct.".formatted(id.toString())
                )
        );
        return RecipeDetailsDTO.fromRecipe(recipe);
    }

    public void deleteById(UUID id) {
        // TODO: delete images
        recipeRepository.deleteById(id);
    }

    @Transactional
    public RecipeDetailsDTO handleCreateRequest(RecipeRequest recipeRequest, String authorSubject) {
        log.info("Attempting create a recipe titled {} with {} images from author {}", recipeRequest.title(), recipeRequest.imageIds().size(), authorSubject);

        if (recipeRepository.existsByTitle(recipeRequest.title())) {
            log.info("Failed to create recipe: a recipe with title {} already exists!", recipeRequest.title());
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RECIPE,
                    "title",
                    "A recipe with title %s already exists!".formatted(recipeRequest.title())
            );
        }

        Recipe recipe = RecipeMapper.toEntity(recipeRequest, authorSubject);

        Recipe saved = recipeRepository.saveAndFlush(recipe);

        imageServiceClient.attach(new HashSet<>(saved.getImageIds()));

        log.info("Recipe with title {} has been created", recipeRequest.title());

        return RecipeDetailsDTO.fromRecipe(saved);
    }

    @Transactional
    public RecipeDetailsDTO handleUpdateRequest(UUID id, RecipeRequest recipeRequest) {
        log.info(
                "Attempting update a recipe titled {} with {} images",
                recipeRequest.title(),
                recipeRequest.imageIds().size()
        );

        Recipe existingRecipe = recipeRepository.findById(id).orElseThrow(() ->
                new BusinessException(
                        ErrorCode.RECIPE_NOT_FOUND,
                        "Did not find any recipe details with ID %s! Ensure the ID is correct.".formatted(id.toString())
                )
        );

        if (!Objects.equals(recipeRequest.id(), id)) {
            log.warn("Recipe ID mismatch detected while updating recipe. Param: {}, request: {}", id, recipeRequest.id());
            throw new BusinessException(
                    ErrorCode.RECIPE_MISMATCH,
                    "The recipe with ID %s does not match the provided ID %s.".formatted(id, recipeRequest.id())
            );
        }

        if (recipeRepository.existsByTitleAndIdIsNot(recipeRequest.title(), existingRecipe.getId())) {
            log.info("Failed to update recipe: a recipe with title {} already exists!", recipeRequest.title());
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RECIPE,
                    "title",
                    "A recipe with title %s already exists!".formatted(recipeRequest.title())
            );
        }

        List<RecipeVariant> patchVariants = recipeRequest.variants().stream()
                        .map(RecipeVariant::new)
                        .toList();

        Set<UUID> existingImageIds = new HashSet<>(existingRecipe.getImageIds());
        Set<UUID> newImageIds = new HashSet<>(recipeRequest.imageIds());
        Set<UUID> imagesToDelete = new HashSet<>(existingImageIds);
        imagesToDelete.removeAll(newImageIds);

        existingRecipe.updateFrom(recipeRequest, patchVariants);

        recipeRepository.saveAndFlush(existingRecipe);

        imageServiceClient.markForDeletion(imagesToDelete);
        imageServiceClient.attach(new HashSet<>(existingRecipe.getImageIds()));

        log.info("Recipe with title {} has been updated", recipeRequest.title());

        return RecipeDetailsDTO.fromRecipe(existingRecipe);
    }

}
