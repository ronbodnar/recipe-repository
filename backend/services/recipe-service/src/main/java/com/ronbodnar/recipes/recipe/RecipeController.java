package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.recipe.dto.RecipeDetailsDTO;

import com.ronbodnar.recipes.recipe.dto.RecipeRequest;
import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequestMapping("/api/v1/recipes")
@RestController
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    @PreAuthorize("hasRole('VIEW-RECIPE')")
    public Page<RecipeSummaryDTO> getAll(@RequestParam(name = "paginationStart", defaultValue = "0") Integer paginationStart,
                                         @RequestParam(name = "paginationLength", defaultValue = "10") Integer paginationLength,
                                         @RequestParam(name = "paginationSortOrder", defaultValue = "id=asc") String paginationSortOrder,
                                         @AuthenticationPrincipal Jwt jwt) {
        UUID authorId = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject(), "JWT subject is missing")
        );
        return recipeService.getAllSummaries(authorId, paginationStart, paginationLength, paginationSortOrder);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CREATE-RECIPE')")
    public RecipeDetailsDTO create(@RequestPart(name = "recipe") @Valid RecipeRequest recipeRequest,
                                   @RequestPart(name = "images", required = false) Optional<List<MultipartFile>> images,
                                   @AuthenticationPrincipal Jwt jwt
    ) {
        UUID authorId = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject(), "JWT subject is missing")
        );

        return recipeService.handleCreateRequest(recipeRequest, images, authorId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDetailsDTO update(@PathVariable("id") UUID id,
                                   @RequestPart(name = "recipe") @Valid RecipeRequest recipeRequest,
                                   @RequestPart(name = "images", required = false) Optional<List<MultipartFile>> images) {
        return recipeService.handleUpdateRequest(id, recipeRequest, images);
    }

    @GetMapping("/{id}")
    public RecipeDetailsDTO getById(@PathVariable("id") UUID id) {
        return recipeService.getById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DELETE-RECIPE')")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        recipeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}