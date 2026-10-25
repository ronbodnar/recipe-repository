package com.ronbodnar.recipes.modules.recipe;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.group.Group;
import com.ronbodnar.recipes.modules.group.GroupService;
import com.ronbodnar.recipes.modules.recipe.domain.RecipeVisibility;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetails;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component("recipeSecurityService")
public class RecipeSecurityService {

    private final Optional<GroupService> groupService;
    private final RecipeRepository recipeRepository;

    public RecipeSecurityService(Optional<GroupService> groupService, RecipeRepository recipeRepository) {
        this.groupService = groupService;
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
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUserDetails user)) {
            return false;
        }

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new BusinessException(ErrorCode.RECIPE_NOT_FOUND));

        if (recipe.getAuthorId().equals(user.getId())) {
            return true;
        }

        if (recipe.getVisibility() == RecipeVisibility.PUBLIC) {
            return true;
        }

        if (groupService.isPresent()) {
            if (recipe.getVisibility() != RecipeVisibility.GROUP) {
                return false;
            }

            List<Group> authorGroups = groupService.get().getAllUserGroups(recipe.getAuthorId());

            return authorGroups.stream().anyMatch(group -> group.isMember(user.getId()));
        }

        return false;
    }

}
