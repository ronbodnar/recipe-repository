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
        WHERE r.authorId = :authorId
    """)
    Page<Recipe> findAllByAuthorWithImages(@Param("authorId") Long authorId, Pageable pageable);

    @Query("""
        SELECT r
        FROM Recipe r
        WHERE r.visibility = 'PUBLIC'
    """)
    Page<Recipe> findAllPublicRecipes(Pageable pageable);

    @Query("""
        SELECT DISTINCT r
        FROM Recipe r
        JOIN GroupMember gm ON gm.userAccount.id = r.authorId
        JOIN Group g ON g.id = gm.group.id
        WHERE r.visibility = 'GROUP'
            AND EXISTS (
                SELECT m.id
                FROM g.members m
                WHERE m.userAccount.id = :userId
            )
    """)
    Page<Recipe> findAllGroupRecipes(Long userId, Pageable pageable);

    @Query("""
        SELECT DISTINCT r
        FROM Recipe r
        JOIN GroupMember gm ON gm.userAccount.id = r.authorId
        WHERE r.visibility = 'GROUP'
          AND gm.group.id = :groupId
    """)
    Page<Recipe> findRecipesForGroup(Long groupId, Pageable pageable);

    @Query("""
        SELECT r.imageIds
        FROM Recipe r
        JOIN r.imageIds
        WHERE r.id = :recipeId
    """)
    List<UUID> findAllImageIdsForRecipeId(@Param("recipeId") UUID recipeId);

    @Query("""
        SELECT r
        FROM Recipe r
        WHERE LOWER(COALESCE(r.title, '')) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(COALESCE(r.description, '')) LIKE LOWER(CONCAT('%', :query, '%'))
           OR EXISTS (
                SELECT 1
                FROM RecipeVariant rv
                JOIN rv.instructions instruction
                WHERE rv.recipe = r
                  AND LOWER(instruction) LIKE LOWER(CONCAT('%', :query, '%'))
           )
    """)
    Page<Recipe> searchRecipes(@Param("query") String query, Pageable pageable);

    boolean existsByTitleAndAuthorId(String title, Long authorId);

    boolean existsByTitleAndAuthorIdAndIdIsNot(String title, Long authorId, UUID id);

}