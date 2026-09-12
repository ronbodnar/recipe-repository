package com.ronbodnar.recipes.modules.recipe;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.recipe.domain.RecipeVisibility;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetails;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("recipeSecurityService")
public class RecipeSecurityService {

    private final RecipeRepository recipeRepository;

    public RecipeSecurityService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public boolean isOwner(UUID recipeId, Authentication authentication) {
        SecurityUserDetails securityUser = (SecurityUserDetails) authentication.getPrincipal();
        if (securityUser == null) {
            return false;
        }

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new BusinessException(ErrorCode.RECIPE_NOT_FOUND));

        return recipe.getAuthorId().equals(securityUser.getId());
    }

    public boolean canView(UUID recipeId, Authentication authentication) {
        SecurityUserDetails securityUser = (SecurityUserDetails) authentication.getPrincipal();
        if (securityUser == null) {
            return false;
        }

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new BusinessException(ErrorCode.RECIPE_NOT_FOUND));

        return recipe.getAuthorId().equals(securityUser.getId())
                || recipe.getVisibility() == RecipeVisibility.PUBLIC;
    }
}
