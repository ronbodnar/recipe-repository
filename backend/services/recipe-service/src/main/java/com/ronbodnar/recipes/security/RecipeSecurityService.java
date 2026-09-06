package com.ronbodnar.recipes.security;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.recipe.Recipe;
import com.ronbodnar.recipes.recipe.RecipeRepository;

import com.ronbodnar.recipes.recipe.domain.RecipeVisibility;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("recipeSecurityService")
public class RecipeSecurityService {

    private final RecipeRepository recipeRepository;

    public RecipeSecurityService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public boolean isOwner(UUID recipeId, JwtAuthenticationToken authentication) {
        // Extract the ID from the token claims
        String subject = authentication.getToken().getSubject();

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new BusinessException(ErrorCode.RECIPE_NOT_FOUND));

        // Return true if the IDs match
        return recipe.getAuthorSubject().equals(subject);
    }

    public boolean canView(UUID recipeId, JwtAuthenticationToken authentication) {
        String subject = authentication.getToken().getSubject();

        if (subject == null) {
            return false;
        }

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new BusinessException(ErrorCode.RECIPE_NOT_FOUND));

        return subject.equals(recipe.getAuthorSubject())
                || recipe.getVisibility() == RecipeVisibility.PUBLIC;
    }
}
