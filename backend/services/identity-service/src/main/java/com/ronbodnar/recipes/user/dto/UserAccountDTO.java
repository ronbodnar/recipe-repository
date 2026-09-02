package com.ronbodnar.recipes.user.dto;

import com.ronbodnar.recipes.user.UserAccount;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserAccountDTO(
        Long id,
        String identityProviderSubject,
        String displayName,
        UUID profileImageId,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {

    public static UserAccountDTO fromEntity(UserAccount userAccount) {
        return new UserAccountDTO(
                userAccount.getId(),
                userAccount.getIdentityProviderSubject(),
                userAccount.getDisplayName(),
                userAccount.getProfileImageId(),
                userAccount.getCreatedAt(),
                userAccount.getLastModifiedAt()
        );
    }
}