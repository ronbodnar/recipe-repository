package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.recipe.dto.RecipeDetailsDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeRequest;
import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;

import com.ronbodnar.recipes.recipe.image.RecipeImage;
import com.ronbodnar.recipes.recipe.image.RecipeImageService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final RecipeImageService recipeImageService;

    public RecipeService(RecipeRepository recipeRepository, RecipeImageService recipeImageService) {
        this.recipeRepository = recipeRepository;
        this.recipeImageService = recipeImageService;
    }

    public Page<RecipeSummaryDTO> getAllSummaries(UUID authorId, int paginationStart, int paginationLength, String paginationSortOrder) {
        Page<Recipe> recipes = recipeRepository.findAllByAuthorId(
                authorId,
                PageRequest.of(paginationStart, paginationLength)
        );

        List<UUID> recipeIds = recipes.getContent().stream()
                .map(Recipe::getId)
                .toList();

        Map<UUID, List<UUID>> imageIdsByRecipeId =
                recipeImageService.findByRecipeIdIn(recipeIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                RecipeImage::getRecipeId,
                                Collectors.mapping(
                                        RecipeImage::getImageId,
                                        Collectors.toList()
                                )
                        ));

        return recipes.map(recipe ->
                new RecipeSummaryDTO(
                        recipe.getId(),
                        recipe.getTitle(),
                        recipe.getDescription(),
                        imageIdsByRecipeId.getOrDefault(recipe.getId(), List.of())
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

        List<UUID> imageIds = recipeImageService.findAllByRecipeId(recipe.getId()).stream().map(RecipeImage::getImageId).toList();

        return RecipeDetailsDTO.fromRecipe(recipe, imageIds);
    }

    public void deleteById(UUID id) {
        recipeRepository.deleteById(id);
    }

    @Transactional
    public RecipeDetailsDTO handleCreateRequest(RecipeRequest recipeRequest,
                                                Optional<List<MultipartFile>> images,
                                                UUID authorId) {
        log.info("Attempting create a recipe titled {} with {} images from author {}", recipeRequest.title(), images.map(List::size).orElse(0), authorId);

        if (recipeRepository.existsByTitle(recipeRequest.title())) {
            log.info("Failed to create recipe: a recipe with title {} already exists!", recipeRequest.title());
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RECIPE,
                    "title",
                    "A recipe with title %s already exists!".formatted(recipeRequest.title())
            );
        }

        Recipe recipe = RecipeMapper.toEntity(recipeRequest, authorId);

        Recipe saved = recipeRepository.save(recipe);

        List<UUID> imageIds = recipeImageService.createImages(saved.getId(), images.orElse(null)).stream()
                .map(RecipeImage::getImageId)
                .toList();

        log.info("Recipe with title {} has been created", recipeRequest.title());

        return RecipeDetailsDTO.fromRecipe(saved, imageIds);
    }

    @Transactional
    public RecipeDetailsDTO handleUpdateRequest(UUID id,
                                                RecipeRequest recipeRequest,
                                                Optional<List<MultipartFile>> images) {
        log.info("Attempting update a recipe titled {} with {} images", recipeRequest.title(), images.map(List::size).orElse(0));

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

        existingRecipe.updateFrom(recipeRequest, patchVariants);

        List<UUID> imageIds = recipeImageService.updateImages(
                existingRecipe.getId(),
                Set.copyOf(recipeRequest.existingImages()),
                images.orElse(null)
        ).stream().map(RecipeImage::getImageId).toList();

        log.info("Recipe with title {} has been updated", recipeRequest.title());

        return RecipeDetailsDTO.fromRecipe(existingRecipe, imageIds);
    }

}
