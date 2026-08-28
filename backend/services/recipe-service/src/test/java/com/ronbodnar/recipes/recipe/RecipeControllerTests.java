package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeDetailsDTO;
import com.ronbodnar.recipes.recipe.domain.CookingMethod;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
public class RecipeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;

    private Recipe recipe;

    private static UUID recipeId;

    @BeforeAll
    public static void setUpAll() {
        recipeId = UUID.randomUUID();
    }

    @BeforeEach
    public void setUp() {
        recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setTitle("Test Recipe");

        RecipeVariant ovenVariant = new RecipeVariant();
        ovenVariant.setId(UUID.randomUUID());
        ovenVariant.setCookingMethod(CookingMethod.OVEN);

        recipe.addVariant(ovenVariant);
    }

    @Test
    public void getAllRecipes() throws Exception {
        given(recipeService.getAllSummaries(0, 10, "id=asc"))
                .willReturn(new PageImpl<>(List.of(RecipeSummaryDTO.fromEntity(recipe, List.of()))));

        mockMvc.perform(get("/recipes")
                .param("paginationStart", "0")
                .param("paginationLength", "10")
                .param("paginationSortOrder", "id=asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                  {
                      "content": [
                        {
                          "id": "%s"
                        }
                      ]
                   }
                """.formatted(recipeId.toString())));
    }

    @Test
    public void testGetById() throws Exception {
        given(recipeService.getById(any())).willReturn(RecipeDetailsDTO.fromRecipe(recipe, List.of(UUID.randomUUID())));
        mockMvc.perform(get("/recipes/" + recipeId))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        Matchers.containsString("Test Recipe")
                ))
                .andExpect(content().json("""
                  {
                    "title": "Test Recipe"
                  }
                """));
    }

    @Test
    public void getById_NotFound() throws Exception {
        given(recipeService.getById(any())).willReturn(null);

        mockMvc.perform(get("/recipes/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void create_returnsCreatedRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setTitle("Test Recipe");
        recipe.setDescription("Test Recipe description");
        RecipeDetailsDTO recipeResponse = RecipeDetailsDTO.fromRecipe(recipe, List.of());

        given(recipeService.handleCreateRequest(any(), any(), any()))
                .willReturn(recipeResponse);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "metadata",
                "",
                "application/json",
                """
                    {
                        "title": "Test Recipe",
                        "description": "Description"
                    }
                """.getBytes()
        );

        MockMultipartFile images = new MockMultipartFile(
                "image.jpg",
                "image.jpg",
                "image/jpeg",
                new byte[] { 1, 2, 3 }
        );

        mockMvc.perform(multipart("/recipes").file(multipartFile).file(images))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Test Recipe")));
    }
}
