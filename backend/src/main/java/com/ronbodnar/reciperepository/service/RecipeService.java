package com.ronbodnar.reciperepository.service;

import com.ronbodnar.reciperepository.enums.*;
import com.ronbodnar.reciperepository.model.recipe.ImageData;
import com.ronbodnar.reciperepository.model.recipe.*;
import com.ronbodnar.reciperepository.model.user.User;
import com.ronbodnar.reciperepository.payload.request.RecipeRequest;
import com.ronbodnar.reciperepository.repository.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RecipeService {

    private final UserService userService;

    private final RecipeRepository recipeRepository;

    private final InstructionRepository instructionRepository;

    private final IngredientRepository ingredientRepository;

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

    public ResponseEntity<?> create(RecipeRequest recipeRequest) {
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> author = userService.getByUsername(authUsername);

        if (author.isEmpty()) {
            return ResponseEntity.badRequest().body("Could not get authenticated user for name:  \"" + authUsername + "\"");
        }

        Recipe recipe = new Recipe();
        Set<Course> courses = new HashSet<>();
        Set<Cuisine> cuisines = new HashSet<>();
        Set<Attribute> attributes = new HashSet<>();
        Set<Instruction> instructions = new HashSet<>();
        Set<RecipeIngredient> ingredients = new HashSet<>();
        Set<ImageData> images = new HashSet<>();

        // Populate the recipe's details
        recipe.setAuthor(author.get());
        recipe.setTitle(recipeRequest.getTitle());
        recipe.setDescription(recipeRequest.getDescription());
        recipe.setCookTime(recipeRequest.getCookTime());
        recipe.setPrepTime(recipeRequest.getPrepTime());
        recipe.setServings(recipeRequest.getServings());
        recipe.setPreparationType(PreparationType.AIR_FRYER);
        recipe.setImages(images);
        recipe.setInstructions(instructions);
        recipe.setIngredients(ingredients);
        recipe.setCourses(courses);
        recipe.setCuisines(cuisines);
        recipe.setAttributes(attributes);

        // Create RecipeImages from the image data and add it to the Recipe's image set.
        recipeRequest.getImageData().forEach(image -> {
            ImageData imageData = new ImageData(image.getUri(), image.getWidth(), image.getHeight());
            images.add(imageData);
        });

        // Create Instructions from the instruction fields and add them to the Recipe's instruction set.
        recipeRequest.getInstructions().forEach(instructionData -> {
            String text = instructionData.getText();
            String imageUri = instructionData.getImageUri();

            Instruction instruction = new Instruction(text, imageUri);
            instructions.add(instruction);
        });

        // Create RecipeIngredients from the ingredient fields and add them to the Recipe's ingredient set.
        recipeRequest.getIngredients().forEach(ingredientData -> {
            double measurement = ingredientData.getMeasurement();
            MeasurementType measurementType = ingredientData.getMeasurementType();

            RecipeIngredient recipeIngredient = new RecipeIngredient(measurement, measurementType);
            recipeIngredient.setIngredient(ingredientRepository.findById(1).orElse(null));
            //TODO: add base/core ingredient
            ingredients.add(recipeIngredient);
        });

        // Courses

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

    public ResponseEntity<?> findById(int id) {
        return ResponseEntity.ok().body("");
    }
}
