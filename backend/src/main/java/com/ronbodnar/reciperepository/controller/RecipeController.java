package com.ronbodnar.reciperepository.controller;

import com.ronbodnar.reciperepository.model.recipe.Recipe;
import com.ronbodnar.reciperepository.payload.request.RecipeRequest;
import com.ronbodnar.reciperepository.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "https://ronbodnar.com"}, allowedHeaders = "*", allowCredentials = "true")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipes/")
    public List<Recipe> getAllRecipes() {
        return recipeService.getAll();
    }

    @PostMapping("recipes/")
    public ResponseEntity<?> addRecipe(@RequestBody RecipeRequest recipeRequest) {
        return recipeService.add(recipeRequest);
    }
}