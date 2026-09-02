package com.ronbodnar.recipes.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    @Query("""
        SELECT r FROM Recipe r LEFT JOIN FETCH r.imageIds WHERE r.authorId = :authorId
    """)
    Page<Recipe> findAllByAuthorIdWithImages(@Param("authorId") UUID authorId, Pageable pageable);

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdIsNot(String title, UUID id);

}