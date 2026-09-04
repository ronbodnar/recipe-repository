package com.ronbodnar.recipes.user.dto;

import com.ronbodnar.recipes.user.UserAccount;

import java.util.UUID;

public record UserAccountSummaryDTO(
        String identityProviderSubject,
        String displayName,
        UUID profileImageId
) {

    public static UserAccountSummaryDTO fromEntity(UserAccount userAccount) {
        return new UserAccountSummaryDTO(
                userAccount.getIdentityProviderSubject(),
                userAccount.getDisplayName(),
                userAccount.getProfileImageId()
        );
    }
}