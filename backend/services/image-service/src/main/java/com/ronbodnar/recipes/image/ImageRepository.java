package com.ronbodnar.recipes.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    List<Image> findAllByIdIn(List<UUID> ids);

    @Query("""
        SELECT i FROM Image i
        WHERE i.status = 'MARKED_FOR_DELETION'
        OR i.status = 'STAGED' AND i.createdAt < :cutoff
    """)
    List<Image> findImagesEligibleForCleanup(@Param("cutoff") LocalDateTime cutoff);

    void deleteAllByIdIn(List<UUID> ids);
}
