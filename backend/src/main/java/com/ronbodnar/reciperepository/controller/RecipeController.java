package com.ronbodnar.reciperepository.controller;

import com.ronbodnar.reciperepository.model.recipe.Recipe;
import com.ronbodnar.reciperepository.payload.request.RecipeRequest;
import com.ronbodnar.reciperepository.service.recipe.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "https://ronbodnar.com"}, allowedHeaders = "*", allowCredentials = "true")
public class RecipeController {

    /**
     * The RecipeService dependency to be injected.
     */
    private final RecipeService recipeService;

    /**
     * Constructor to inject the RecipeService dependency.
     * @param recipeService The RecipeService bean to inject.
     */
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipes/")
    public List<Recipe> getAll() {
        return recipeService.getAll();
    }

    @PostMapping("/recipes/")
    public ResponseEntity<?> create(@RequestBody RecipeRequest recipeRequest) {
        return recipeService.create(recipeRequest);
    }
}