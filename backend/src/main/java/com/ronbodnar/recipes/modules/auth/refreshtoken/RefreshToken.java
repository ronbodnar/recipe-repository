package com.ronbodnar.recipes.modules.auth.refreshtoken;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_token",
        indexes = {
                @Index(columnList = "user_id, token_hash")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", nullable = false, columnDefinition = "BINARY(16)")
    private byte[] deviceId;

    @Column(name = "token_hash", nullable = false, columnDefinition = "BINARY(32)")
    private byte[] tokenHash;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "valid", nullable = false, columnDefinition = "TINYINT", length = 1)
    private boolean valid;

    public RefreshToken(Long userId, byte[] deviceId, byte[] tokenHash, String ipAddress, Instant issuedAt, Instant expiresAt) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.tokenHash = tokenHash;
        this.ipAddress = ipAddress;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.valid = true;
    }

}
