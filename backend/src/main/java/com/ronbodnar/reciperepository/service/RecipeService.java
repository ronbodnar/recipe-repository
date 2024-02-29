package com.ronbodnar.reciperepository.service;

import com.ronbodnar.reciperepository.enums.MeasurementType;
import com.ronbodnar.reciperepository.enums.PreparationType;
import com.ronbodnar.reciperepository.model.recipe.*;
import com.ronbodnar.reciperepository.model.user.User;
import com.ronbodnar.reciperepository.payload.request.RecipeRequest;
import com.ronbodnar.reciperepository.repository.*;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RecipeService {

    private final UserService userService;
    private final RecipeRepository recipeRepository;

    private final IngredientRepository ingredientRepository;

    private final InstructionRepository instructionRepository;

    private final RecipeImageRepository recipeImageRepository;

    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeService(UserService userService, RecipeRepository recipeRepository, IngredientRepository ingredientRepository, InstructionRepository instructionRepository, RecipeImageRepository recipeImageRepository, RecipeIngredientRepository recipeIngredientRepository) {
        this.userService = userService;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.instructionRepository = instructionRepository;
        this.recipeImageRepository = recipeImageRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    public List<Recipe> getAll() {
        return (List<Recipe>) recipeRepository.findAll();
    }

    public void add(Recipe recipe) {
        recipeRepository.save(recipe);
    }

    public ResponseEntity<?> add(RecipeRequest recipeRequest) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> author = userService.getByUsername(authUsername);

        if (author.isEmpty()) {
            return ResponseEntity.badRequest().body("Could not get authenticated user for name:  \"" + authUsername + "\"");
        }

        Recipe recipe = new Recipe();
        Set<RecipeImage> images = new HashSet<>();
        Set<Instruction> instructions = new HashSet<>();
        Set<RecipeIngredient> ingredients = new HashSet<>();

        // Populate the recipe's details
        recipe.setAuthor(author.get());
        recipe.setTitle(recipeRequest.getTitle());
        recipe.setDescription(recipeRequest.getDescription());
        recipe.setCookTime(recipeRequest.getCookTime());
        recipe.setPrepTime(recipeRequest.getPrepTime());
        recipe.setServings(recipeRequest.getServings());
        recipe.setImages(images);
        recipe.setInstructions(instructions);
        recipe.setIngredients(ingredients);

        // Create RecipeImages from the image data and add it to the Recipe's image set.
        recipeRequest.getImageData().forEach(image -> {
            RecipeImage recipeImage = new RecipeImage(recipe, image.getImageData());
            images.add(recipeImage);
        });

        // Create Instructions from the instruction fields and add them to the Recipe's instruction set.
        recipeRequest.getInstructions().forEach(instructionData -> {
            int stepNumber = instructionData.getStepNumber();
            String content = instructionData.getContent();
            String imageData = instructionData.getImageData();
            PreparationType preparationType = instructionData.getPreparationType();

            Instruction instruction = new Instruction(recipe, stepNumber, content, preparationType, imageData);
            instructions.add(instruction);
        });

        // Create RecipeIngredients from the ingredient fields and add them to the Recipe's ingredient set.
        recipeRequest.getIngredients().forEach(ingredientData -> {
            double quantity = ingredientData.getQuantity();
            MeasurementType measurementType = ingredientData.getMeasurementType();

            RecipeIngredient recipeIngredient = new RecipeIngredient(quantity, measurementType);
            //TODO: add base/core ingredient
            ingredients.add(recipeIngredient);
        });

        /*RecipeImage image1 = new RecipeImage();
        RecipeImage image2 = new RecipeImage();

        image1.setImageData("Base64:1");
        image1.setRecipe(recipe);

        image2.setImageData("Base64:2");
        image2.setRecipe(recipe);

        images.add(image1);
        images.add(image2);*/

        // INSTRUCTIONS
        /*Instruction instruction1 = new Instruction();
        instruction1.setStepNumber(0);
        instruction1.setRecipe(recipe);
        instruction1.setContent("This is the first step.");
        instruction1.setPreparationType(PreparationType.AIR_FRYER);

        Instruction instruction2 = new Instruction();
        instruction2.setStepNumber(1);
        instruction2.setRecipe(recipe);
        instruction2.setContent("This is the second step.");
        instruction2.setPreparationType(PreparationType.AIR_FRYER);

        Instruction instruction3 = new Instruction();
        instruction3.setStepNumber(0);
        instruction3.setRecipe(recipe);
        instruction3.setContent("This is the first step.");
        instruction3.setPreparationType(PreparationType.OVEN);

        instructions.add(instruction1);
        instructions.add(instruction2);
        instructions.add(instruction3);*/

        // INGREDIENTS
        /*Optional<Ingredient> chickenBreast = ingredientRepository.findById(1);

        Optional<Ingredient> tomatoSauce = ingredientRepository.findById(2);

        RecipeIngredient ingredient1 = new RecipeIngredient();
        ingredient1.setIngredient(chickenBreast.orElse(null));
        ingredient1.setQuantity(1);
        ingredient1.setMeasurementType(MeasurementType.POUND);

        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient2.setIngredient(tomatoSauce.orElse(null));
        ingredient2.setQuantity(10);
        ingredient2.setMeasurementType(MeasurementType.OUNCE);

        ingredients.add(ingredient1);
        ingredients.add(ingredient2);*/

        // Save the Recipe to the repository
        add(recipe);

        // Save all new RecipeImages to the repository
        recipeImageRepository.saveAll(images);

        // Save all new Instructions to the repository
        instructionRepository.saveAll(instructions);

        // Save all new Ingredients to the repository
        recipeIngredientRepository.saveAll(ingredients);

        return ResponseEntity.ok().body(recipeRequest);
    }
}
