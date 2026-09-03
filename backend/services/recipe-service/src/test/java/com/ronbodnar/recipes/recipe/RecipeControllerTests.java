package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.recipe.dto.RecipeDetailsDTO;
import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.ConfigurableSmartRequestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(RecipeController.class)
class RecipeControllerTests {

    private final String RECIPE_API_URL = "/api/v1/recipes";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;

    private Recipe recipe;

    private static String authorId;

    private static UUID recipeId;

    @BeforeAll
    static void setUpAll() {
        recipeId = UUID.randomUUID();
        authorId = UUID.randomUUID().toString();
    }

    @BeforeEach
    void setUp() {
        recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setTitle("Test Recipe A");
        recipe.setAuthorId(authorId);
    }

    private ResultActions performAuthenticatedRequest(
            ConfigurableSmartRequestBuilder<?> builder,
            String authorId,
            String... authorities
    ) throws Exception {
        return mockMvc.perform(builder
                .with(jwt()
                        .jwt(jwt -> jwt.subject(authorId))
                        .authorities(
                                Arrays.stream(authorities)
                                        .map(SimpleGrantedAuthority::new)
                                        .toArray(SimpleGrantedAuthority[]::new)
                        )
                ));
    }

    @Test
    void getAllRecipes_returnsRecipeByAuthorId() throws Exception {
        given(recipeService.getAllSummaries(
                eq(recipe.getAuthorId()),
                eq(0),
                eq(10),
                eq("id=asc")
        )).willReturn(new PageImpl<>(
                List.of(RecipeSummaryDTO.fromEntity(recipe, List.of()))
        ));

        performAuthenticatedRequest(
                get(RECIPE_API_URL)
                        .param("paginationStart", "0")
                        .param("paginationLength", "10")
                        .param("paginationSortOrder", "id=asc"),
                recipe.getAuthorId(),
                "ROLE_VIEW-RECIPE"
        )
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
                    """.formatted(recipeId)));

        then(recipeService).should().getAllSummaries(
                recipe.getAuthorId(),
                0,
                10,
                "id=asc"
        );
    }

    @Test
    void getById_returnsRecipeDetails_whenAuthenticated() throws Exception {
        given(recipeService.getById(recipe.getId()))
                .willReturn(RecipeDetailsDTO.fromRecipe(recipe));

        performAuthenticatedRequest(
                get(RECIPE_API_URL + "/{id}", recipe.getId()),
                recipe.getAuthorId(),
                "ROLE_VIEW-RECIPE"
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                {
                    "id": "%s",
                    "title": "Test Recipe A"
                }
                """.formatted(recipe.getId())));

        then(recipeService).should().getById(recipe.getId());
    }

    @Test
    void getById_whenRecipeDoesNotExist_returnsNotFound() throws Exception {
        UUID requestedRecipeId = UUID.randomUUID();

        given(recipeService.getById(requestedRecipeId))
                .willThrow(new BusinessException(ErrorCode.RECIPE_NOT_FOUND));

        performAuthenticatedRequest(
                get(RECIPE_API_URL + "/{id}", requestedRecipeId),
                UUID.randomUUID().toString(),
                "ROLE_VIEW-RECIPE"
        )
                .andExpect(status().isNotFound());

        then(recipeService).should().getById(requestedRecipeId);
    }

    @Test
    void create_returnsCreatedRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setTitle("Test Recipe");
        recipe.setDescription("Test Recipe description");

        given(recipeService.handleCreateRequest(any(), any()))
                .willReturn(RecipeDetailsDTO.fromRecipe(recipe));

        MockMultipartFile multipartFile = new MockMultipartFile(
                "recipe",
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

        performAuthenticatedRequest(
                multipart(RECIPE_API_URL).file(multipartFile).file(images),
                UUID.randomUUID().toString()
        )
                .andExpect(status().isCreated())
                .andExpect(content().string(Matchers.containsString("Test Recipe")));
    }
}
