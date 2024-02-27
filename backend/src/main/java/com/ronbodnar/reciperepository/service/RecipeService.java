package com.ronbodnar.reciperepository.service;

import com.ronbodnar.reciperepository.model.recipe.Instruction;
import com.ronbodnar.reciperepository.model.recipe.Recipe;
import com.ronbodnar.reciperepository.model.recipe.RecipeIngredient;
import com.ronbodnar.reciperepository.payload.request.RecipeRequest;
import com.ronbodnar.reciperepository.repository.RecipeRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAll() {
        return (List<Recipe>) recipeRepository.findAll();
    }

    public ResponseEntity<?> add(RecipeRequest recipeRequest) {
        Set<RecipeIngredient> ingredients = new HashSet<>();
        Set<Instruction> instructions = new HashSet<>();


        return ResponseEntity.ok().body(recipeRequest);
    }
}
