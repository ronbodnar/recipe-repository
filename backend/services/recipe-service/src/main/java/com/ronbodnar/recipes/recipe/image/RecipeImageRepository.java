package com.ronbodnar.recipes.recipe.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeImageRepository extends JpaRepository<RecipeImage, UUID> {

    List<RecipeImage> findByRecipeId(UUID recipeId);

    List<RecipeImage> findByRecipeIdIn(List<UUID> recipeIds);

    void deleteAllByImageIdIn(List<UUID> imageIds);
}
