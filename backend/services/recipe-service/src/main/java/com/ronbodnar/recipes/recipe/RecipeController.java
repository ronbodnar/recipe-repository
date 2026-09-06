package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.recipe.dto.RecipeDetailsDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeRequest;
import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
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
    public Page<RecipeSummaryDTO> getAll(Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        String authorSubject = Objects.requireNonNull(jwt.getSubject(), "JWT subject is missing");
        return recipeService.getAllSummaries(authorSubject, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CREATE-RECIPE')")
    public RecipeDetailsDTO create(@RequestBody @Valid RecipeRequest recipeRequest, @AuthenticationPrincipal Jwt jwt) {
        String authorSubject = Objects.requireNonNull(jwt.getSubject(), "JWT subject is missing");

        return recipeService.handleCreateRequest(recipeRequest, authorSubject);
    }

    @GetMapping("/discover")
    @PreAuthorize("hasRole('VIEW-RECIPE')")
    public Page<RecipeSummaryDTO> getDiscoverRecipes(Pageable pageable) {
        return recipeService.getDiscoverRecipes(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDetailsDTO update(@PathVariable("id") UUID id, @RequestBody @Valid RecipeRequest recipeRequest) {
        return recipeService.handleUpdateRequest(id, recipeRequest);
    }

    @GetMapping("/{id}")
    public RecipeDetailsDTO getById(@PathVariable("id") UUID id) {
        return recipeService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('DELETE-RECIPE')")
    public void delete(@PathVariable("id") UUID id) {
        recipeService.deleteById(id);
    }
}