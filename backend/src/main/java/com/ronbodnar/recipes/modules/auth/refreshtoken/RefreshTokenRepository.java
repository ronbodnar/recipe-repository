package com.ronbodnar.recipes.modules.auth.refreshtoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(byte[] tokenHash);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken SET valid = FALSE WHERE deviceId = :deviceId")
    void invalidateTokensForDeviceId(@Param("deviceId") byte[] deviceId);
}
