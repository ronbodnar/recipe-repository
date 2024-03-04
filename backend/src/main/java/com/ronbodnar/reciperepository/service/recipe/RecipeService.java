package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.model.recipe.ImageData;
import com.ronbodnar.reciperepository.model.recipe.*;
import com.ronbodnar.reciperepository.model.user.User;
import com.ronbodnar.reciperepository.payload.request.RecipeRequest;

import com.ronbodnar.reciperepository.repository.recipe.*;
import com.ronbodnar.reciperepository.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecipeService {

    private final UserService userService;

    private final ImageDataService imageDataService;

    private final InstructionService instructionService;

    private final RecipeIngredientService recipeIngredientService;

    private final MealTypeRepository mealTypeRepository;

    private final CuisineRepository cuisineRepository;

    private final AttributeRepository attributeRepository;

    private final RecipeRepository recipeRepository;

    private final InstructionRepository instructionRepository;

    private final IngredientRepository ingredientRepository;

    private final RecipeIngredientRepository recipeIngredientRepository;

    private final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    public RecipeService(UserService userService, ImageDataService imageDataService, RecipeIngredientService recipeIngredientService, InstructionService instructionService, RecipeRepository recipeRepository, IngredientRepository ingredientRepository, InstructionRepository instructionRepository, RecipeIngredientRepository recipeIngredientRepository, MealTypeRepository mealTypeRepository, CuisineRepository cuisineRepository, AttributeRepository attributeRepository) {
        this.userService = userService;
        this.imageDataService = imageDataService;
        this.instructionService = instructionService;
        this.recipeIngredientService = recipeIngredientService;

        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.instructionRepository = instructionRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.mealTypeRepository = mealTypeRepository;
        this.cuisineRepository = cuisineRepository;
        this.attributeRepository = attributeRepository;
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
        Set<MealType> mealTypes = new HashSet<>();
        Set<Cuisine> cuisines = new HashSet<>();
        Set<Attribute> attributes = new HashSet<>();
        List<Instruction> instructions = new ArrayList<>();
        List<RecipeIngredient> ingredients = new ArrayList<>();
        Set<ImageData> images = new HashSet<>();
        Set<PreparationType> preparationTypes = new HashSet<>();

        // Populate the recipe's details
        recipe.setAuthor(author.get());
        recipe.setTitle(recipeRequest.getTitle());
        recipe.setDescription(recipeRequest.getDescription());
        recipe.setCookTime(recipeRequest.getCookTime());
        recipe.setPrepTime(recipeRequest.getPrepTime());
        recipe.setServings(recipeRequest.getServings());
        recipe.setPreparationTypes(preparationTypes);
        recipe.setImages(images);
        recipe.setInstructions(instructions);
        recipe.setIngredients(ingredients);
        recipe.setMealTypes(mealTypes);
        recipe.setCuisines(cuisines);
        recipe.setAttributes(attributes);

        /*
         * Create RecipeImages from the image data and add it to the Recipe's image set.
         */
        List<ImageData> imageData = new ArrayList<>();//imageDataService.createAll(recipeRequest.getImageData());
        images.addAll(imageData);

        /*
         * Create Instructions from the instruction fields and add them to the Recipe's instruction set.
         */
        List<Instruction> instructionData = instructionService.createAll(recipeRequest.getInstructions());
        instructions.addAll(instructionData);

        /*
         * Create RecipeIngredients from the ingredient fields and add them to the Recipe's ingredient set.
         */
        List<RecipeIngredient> ingredientData = recipeIngredientService.createAll(recipe, recipeRequest.getIngredients());
        if (ingredientData.size() == recipeRequest.getIngredients().size()) // TODO: exception handling
            ingredients.addAll(ingredientData);

        // Meal Types
       /* recipeRequest.getMealTypes().forEach(mealTypeData -> {
            MealType.Kind kind = mealTypeData.getKind();

            Optional<MealType> mealType = mealTypeRepository.findByKind(null);

            mealType.ifPresentOrElse(mealTypes::add, () -> { logger.error("Trying to create recipe with unknown meal type!"); });
        });

        // Cuisines
        recipeRequest.getCuisines().forEach(cuisineData -> {
            Cuisine.Kind kind = cuisineData.getKind();

            Optional<Cuisine> cuisine = cuisineRepository.findByCuisineType(kind);

            cuisine.ifPresentOrElse(cuisines::add, () -> { logger.error("Trying to create recipe with unknown cuisine!"); });
        });

        // Attributes
        recipeRequest.getAttributes().forEach(attributeData -> {
            Attribute.Kind kind = attributeData.getKind();

            Optional<Attribute> attribute = attributeRepository.findByAttributeType(kind);

            attribute.ifPresentOrElse(attributes::add, () -> { logger.error("Trying to create recipe with unknown attribute!"); });
        });*/

        // Save the Recipe to the repository
        add(recipe);

        // Save all new RecipeImages to the repository
        imageDataService.saveAll(images);

        // Save all new Instructions to the repository
        instructionService.saveAll(instructions);

        // Save all new Ingredients to the repository
        recipeIngredientService.saveAll(ingredients);

        return ResponseEntity.ok().body(recipeRequest);
    }

    public ResponseEntity<?> findById(int id) {
        return ResponseEntity.ok().body("");
    }
}
