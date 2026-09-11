package com.ronbodnar.recipes.modules.identity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Query("""
        SELECT a FROM UserAccount a LEFT JOIN FETCH a.roles WHERE a.id = :id
    """)
    Optional<UserAccount> findByIdWithRoles(@Param("id") Long id);

    Optional<UserAccount> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}