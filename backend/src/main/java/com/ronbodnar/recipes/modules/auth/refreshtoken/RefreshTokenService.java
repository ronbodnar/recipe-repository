package com.ronbodnar.recipes.modules.auth.refreshtoken;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.identity.user.UserAccount;
import com.ronbodnar.recipes.modules.identity.user.UserAccountService;
import com.ronbodnar.recipes.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final JwtService jwtService;
    private final UserAccountService userAccountService;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(JwtService jwtService, UserAccountService userAccountService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void createFromToken(String token, String ip) {
        Claims claims = jwtService.getClaims(token);
        Long userId = claims.get("id", Long.class);
        Instant issuedAt = claims.getIssuedAt().toInstant();
        Instant expiresAt = claims.getExpiration().toInstant();
        byte[] hashedToken = sha256(token);

        String deviceIdClaim = claims.get("deviceId", String.class);
        UUID deviceId = UUID.fromString(deviceIdClaim);
        byte[] deviceIdBytes = toBytes(deviceId);

        invalidateDeviceId(deviceIdBytes);

        RefreshToken refreshToken = new RefreshToken(
                userId,
                deviceIdBytes,
                hashedToken,
                ip,
                issuedAt,
                expiresAt
        );
        refreshTokenRepository.save(refreshToken);
    }

    public UserAccount validateAndGetUser(String refreshToken) {
        byte[] hash = sha256(refreshToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash).orElseThrow(() ->
                new BusinessException(
                        ErrorCode.INVALID_REFRESH_TOKEN,
                        "The provided refresh token does not exist in the database."
                )
        );

        if (!token.isValid() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(
                    ErrorCode.INVALID_REFRESH_TOKEN,
                    "The refresh token is invalid. expiresAt=" + token.getExpiresAt().toString()
            );
        }

        return userAccountService.getById(token.getUserId());
    }

    private void invalidateDeviceId(byte[] deviceId) {
        refreshTokenRepository.invalidateTokensForDeviceId(deviceId);
    }

    private static byte[] sha256(String token) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_ERROR,
                    "Unsupported token hash algorithm: " + e.getMessage()
            );
        }
        return messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] toBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
