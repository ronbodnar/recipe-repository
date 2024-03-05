package com.ronbodnar.reciperepository.recipe;

import com.ronbodnar.reciperepository.attribute.Attribute;
import com.ronbodnar.reciperepository.attribute.AttributeService;
import com.ronbodnar.reciperepository.cuisine.Cuisine;
import com.ronbodnar.reciperepository.cuisine.CuisineService;
import com.ronbodnar.reciperepository.imagedata.ImageData;
import com.ronbodnar.reciperepository.imagedata.ImageDataService;
import com.ronbodnar.reciperepository.ingredient.Ingredient;
import com.ronbodnar.reciperepository.ingredient.IngredientService;
import com.ronbodnar.reciperepository.instruction.Instruction;
import com.ronbodnar.reciperepository.instruction.InstructionService;
import com.ronbodnar.reciperepository.mealtype.MealType;
import com.ronbodnar.reciperepository.mealtype.MealTypeService;
import com.ronbodnar.reciperepository.preparationtype.PreparationType;
import com.ronbodnar.reciperepository.preparationtype.PreparationTypeService;
import com.ronbodnar.reciperepository.user.User;
import com.ronbodnar.reciperepository.payload.request.RecipeRequest;

import com.ronbodnar.reciperepository.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecipeService {

    private final UserService userService;

    private final CuisineService cuisineService;

    private final MealTypeService mealTypeService;

    private final AttributeService attributeService;

    private final ImageDataService imageDataService;

    private final RecipeRepository recipeRepository;

    private final IngredientService ingredientService;

    private final InstructionService instructionService;

    private final PreparationTypeService preparationTypeService;

    private final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    public RecipeService(UserService userService, CuisineService cuisineService, MealTypeService mealTypeService, AttributeService attributeService, ImageDataService imageDataService, RecipeRepository recipeRepository, IngredientService ingredientService, InstructionService instructionService, PreparationTypeService preparationTypeService) {
        this.userService = userService;
        this.cuisineService = cuisineService;
        this.mealTypeService = mealTypeService;
        this.attributeService = attributeService;
        this.imageDataService = imageDataService;
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
        this.instructionService = instructionService;
        this.preparationTypeService = preparationTypeService;
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
        List<Ingredient> ingredients = new ArrayList<>();
        Set<ImageData> images = new HashSet<>();
        Set<PreparationType> preparationTypes = new HashSet<>();

        // Populate the recipe's details from the request
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
         * Create a list of ImageData from the submitted image data and add it to the Recipe's image set.
         */
        List<ImageData> imageData = new ArrayList<>();//imageDataService.createAll(recipeRequest.getImageData());
        images.addAll(imageData);

        /*
         * Create Instructions from the instruction fields and add them to the Recipe's instruction set.
         */
        List<Instruction> instructionData = instructionService.createAll(recipeRequest.getInstructions());
        if (instructionData.size() == recipeRequest.getInstructions().size())
            instructions.addAll(instructionData);
        else
            logger.error("Instruction exception");

        /*
         * Create Ingredients from the ingredient fields and add them to the Recipe's ingredient set.
         */
        List<Ingredient> ingredientData = ingredientService.createAll(recipe, recipeRequest.getIngredients());
        if (ingredientData.size() == recipeRequest.getIngredients().size()) // TODO: exception handling
            ingredients.addAll(ingredientData);
        else
            logger.error("Ingredient exception");

        /*
         * Create associated MealTypes from the meal type fields and add them to the Recipe's mealType set.
         */
        Set<MealType> mealTypeData = mealTypeService.createAll(recipe, recipeRequest.getMealTypes());
        if (mealTypeData.size() == recipeRequest.getMealTypes().size())
            mealTypes.addAll(mealTypeData);
        else
            logger.error("MealType exception");

        /*
         * Create associated Cuisines from the cuisine fields and add them to the Recipe's cuisines set.
         */
        Set<Cuisine> cuisineData = cuisineService.createAll(recipe, recipeRequest.getCuisines());
        if (cuisineData.size() == recipeRequest.getCuisines().size())
            cuisines.addAll(cuisineData);
        else
            logger.error("Cuisine exception");

        /*
         * Create associated Attributes from the attribute fields and add them to the Recipe's attribute set.
         */
        Set<Attribute> attributeData = attributeService.createAll(recipe, recipeRequest.getAttributes());
        if (attributeData.size() == recipeRequest.getAttributes().size())
            attributes.addAll(attributeData);
        else
            logger.error("Attribute exception");

        /*
         * Create associated PreparationTypes from the attribute fields and add them to the Recipe's preparationType set.
         */
        Set<PreparationType> preparationTypeData = preparationTypeService.createAll(recipe, recipeRequest.getPreparationTypes());
        if (preparationTypeData.size() == recipeRequest.getPreparationTypes().size())
            preparationTypes.addAll(preparationTypeData);
        else
            logger.error("PreparationType exception");

        // Save the Recipe to the repository
        this.add(recipe);

        // Save all new RecipeImages to the repository
        imageDataService.saveAll(images);

        // Save all new Instructions to the repository
        instructionService.saveAll(instructions);

        // Save all new Ingredients to the repository
        ingredientService.saveAll(ingredients);

        return ResponseEntity.ok().body(recipeRequest);
    }

    public ResponseEntity<?> findById(int id) {
        return ResponseEntity.ok().body("");
    }
}
