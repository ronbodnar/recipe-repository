package com.ronbodnar.recipes.modules.recipe;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.image.ImageService;
import com.ronbodnar.recipes.modules.recipe.dto.RecipeDetailsDTO;
import com.ronbodnar.recipes.modules.recipe.dto.RecipeRequest;
import com.ronbodnar.recipes.modules.recipe.dto.RecipeSummaryDTO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final ImageService imageService;

    public RecipeService(RecipeRepository recipeRepository, ImageService imageService) {
        this.recipeRepository = recipeRepository;
        this.imageService = imageService;
    }

    public Page<RecipeSummaryDTO> getAllSummaries(Long authorId, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findAllByAuthorWithImages(
                authorId,
                pageable
        );

        return toSummaryPage(recipes);
    }

    public Page<RecipeSummaryDTO> getDiscoverRecipes(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findAllPublicRecipes(pageable);

        return toSummaryPage(recipes);
    }

    @Transactional(readOnly = true)
    public RecipeDetailsDTO getById(UUID id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() ->
                new BusinessException(
                        ErrorCode.RECIPE_NOT_FOUND,
                        "Did not find any recipe details with ID %s! Ensure the ID is correct.".formatted(id.toString())
                )
        );
        return RecipeDetailsDTO.from(recipe);
    }

    public void deleteById(UUID id) {
        List<UUID> imageIds = recipeRepository.findAllImageIdsForRecipeId(id);

        imageService.markForDeletion(imageIds);

        recipeRepository.deleteById(id);
    }

    @Transactional
    public RecipeDetailsDTO handleCreateRequest(RecipeRequest recipeRequest, Long authorId) {
        log.info("Attempting create a recipe titled {} with {} images from author {}",
                recipeRequest.title(), recipeRequest.imageIds().size(), authorId);

        if (recipeRepository.existsByTitleAndAuthorId(recipeRequest.title(), authorId)) {
            log.info("Failed to create recipe: a recipe with title {} already exists!", recipeRequest.title());
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RECIPE,
                    "title",
                    "A recipe with title %s already exists!".formatted(recipeRequest.title())
            );
        }

        Recipe recipe = RecipeMapper.toEntity(recipeRequest, authorId);

        Recipe saved = recipeRepository.saveAndFlush(recipe);

        imageService.attach(saved.getImageIds());

        log.info("Recipe with title {} has been created", recipeRequest.title());

        return RecipeDetailsDTO.from(saved);
    }

    @Transactional
    public RecipeDetailsDTO handleUpdateRequest(UUID id, RecipeRequest recipeRequest) {
        log.info(
                "Attempting to update a recipe titled {} with {} images",
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
            log.warn("Recipe ID mismatch detected while updating recipe. Param: {}, request: {}",
                    id, recipeRequest.id());
            throw new BusinessException(
                    ErrorCode.RECIPE_MISMATCH,
                    "The recipe with ID %s does not match the provided ID %s.".formatted(id, recipeRequest.id())
            );
        }

        if (recipeRepository.existsByTitleAndAuthorIdAndIdIsNot(recipeRequest.title(), existingRecipe.getAuthorId(), existingRecipe.getId())) {
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

        List<UUID> existingImageIds = existingRecipe.getImageIds();
        Set<UUID> newImageIds = new HashSet<>(recipeRequest.imageIds());
        Set<UUID> imagesToDelete = new HashSet<>(existingImageIds);
        imagesToDelete.removeAll(newImageIds);

        existingRecipe.updateFrom(recipeRequest, patchVariants);

        recipeRepository.saveAndFlush(existingRecipe);

        imageService.markForDeletion(List.copyOf(imagesToDelete));
        imageService.attach(existingRecipe.getImageIds());

        log.info("Recipe with title {} has been updated", recipeRequest.title());

        return RecipeDetailsDTO.from(existingRecipe);
    }

    private Page<RecipeSummaryDTO> toSummaryPage(Page<Recipe> recipes) {
        return recipes.map(recipe ->
                new RecipeSummaryDTO(
                        recipe.getId(),
                        recipe.getTitle(),
                        recipe.getDescription(),
                        recipe.getImageIds()
                )
        );
    }

}
