package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.image.Image;
import com.ronbodnar.recipes.recipe.dto.RecipeSummaryDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdIsNot(String title, UUID id);

}