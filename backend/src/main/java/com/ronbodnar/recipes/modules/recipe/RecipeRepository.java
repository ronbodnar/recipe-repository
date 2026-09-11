package com.ronbodnar.recipes.modules.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    @Query("""
        SELECT r
        FROM Recipe r
        LEFT JOIN FETCH r.imageIds
        WHERE r.authorId = :authorId
    """)
    Page<Recipe> findAllByAuthorWithImages(@Param("authorId") Long authorId, Pageable pageable);

    @Query("""
        SELECT r
        FROM Recipe r
        LEFT JOIN FETCH r.imageIds
        WHERE r.visibility = 'PUBLIC'
    """)
    Page<Recipe> findAllPublicRecipes(Pageable pageable);

    @Query("""
        SELECT r.imageIds
        FROM Recipe r
        JOIN r.imageIds
        WHERE r.id = :recipeId
    """)
    List<UUID> findAllImageIdsForRecipeId(@Param("recipeId") UUID recipeId);

    boolean existsByTitleAndAuthorId(String title, Long authorId);

    boolean existsByTitleAndAuthorIdAndIdIsNot(String title, Long authorId, UUID id);

}