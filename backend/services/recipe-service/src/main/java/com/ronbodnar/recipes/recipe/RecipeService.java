package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.recipe.dto.RecipeDetailsDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeRequest;
import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.springframework.security.oauth2.client.web.ClientAttributes.clientRegistrationId;

@Slf4j
@Service
public class RecipeService {

    private final RestClient restClient;

    private final RecipeRepository recipeRepository;

    public RecipeService(RestClient restClient, RecipeRepository recipeRepository) {
        this.restClient = restClient;
        this.recipeRepository = recipeRepository;
    }

    public Page<RecipeSummaryDTO> getAllSummaries(UUID authorId, int paginationStart, int paginationLength, String paginationSortOrder) {
        Page<Recipe> recipes = recipeRepository.findAllByAuthorIdWithImages(
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
        recipeRepository.deleteById(id);
    }

    @Transactional
    public RecipeDetailsDTO handleCreateRequest(RecipeRequest recipeRequest, UUID authorId) {
        log.info("Attempting create a recipe titled {} with {} images from author {}", recipeRequest.title(), recipeRequest.imageIds().size(), authorId);

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

        attachImages(saved.getImageIds());

        log.info("Recipe with title {} has been created", recipeRequest.title());

        return RecipeDetailsDTO.fromRecipe(saved);
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

        attachImages(existingRecipe.getImageIds());

        log.info("Recipe with title {} has been updated", recipeRequest.title());

        return RecipeDetailsDTO.fromRecipe(existingRecipe);
    }

    private void attachImages(List<UUID> imageIds) {
        restClient.post()
                .uri("http://localhost:8080/api/v1/images/attach")
                .attributes(clientRegistrationId("keycloak"))
                .body(imageIds)
                .retrieve()
                .toBodilessEntity();
    }

}
