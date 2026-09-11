package com.ronbodnar.recipes.modules.identity.user.dto;

import com.ronbodnar.recipes.modules.identity.user.UserAccount;

import java.util.UUID;

public record UserAccountSummaryDTO(
        Long id,
        String username,
        UUID profileImageId
) {

    public static UserAccountSummaryDTO from(UserAccount userAccount) {
        return new UserAccountSummaryDTO(
                userAccount.getId(),
                userAccount.getUsername(),
                userAccount.getProfileImageId()
        );
    }
}