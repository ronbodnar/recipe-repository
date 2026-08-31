package com.ronbodnar.recipes.user.dto;

import com.ronbodnar.recipes.user.UserAccount;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserAccountDTO(
        Long id,
        UUID keycloakSubject,
        UUID profileImageId,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {

    public static UserAccountDTO fromEntity(UserAccount userAccount) {
        return new UserAccountDTO(
                userAccount.getId(),
                userAccount.getKeycloakSubject(),
                userAccount.getProfileImageId(),
                userAccount.getCreatedAt(),
                userAccount.getLastModifiedAt()
        );
    }
}