package com.ronbodnar.recipes.modules.identity.user.dto;

import com.ronbodnar.recipes.modules.identity.role.Role;
import com.ronbodnar.recipes.modules.identity.user.UserAccount;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserAccountDTO(
        Long id,
        String username,
        String email,
        String givenName,
        String familyName,
        UUID profileImageId,
        Set<String> roles
) {

    public static UserAccountDTO from(UserAccount userAccount) {
        return new UserAccountDTO(
                userAccount.getId(),
                userAccount.getUsername(),
                userAccount.getEmail(),
                userAccount.getGivenName(),
                userAccount.getFamilyName(),
                userAccount.getProfileImageId(),
                userAccount.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }
}