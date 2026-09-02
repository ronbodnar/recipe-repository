package com.ronbodnar.recipes.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByIdentityProviderSubject(String subject);

    @Query("""
        SELECT COUNT(u) > 0
        FROM UserAccount u
        WHERE u.displayName = :displayName
          AND u.identityProviderSubject <> :identityProviderSubject
    """)
    boolean existsDisplayNameUsedByAnotherUser(
            @Param("displayName") String displayName,
            @Param("identityProviderSubject") String identityProviderSubject
    );
}