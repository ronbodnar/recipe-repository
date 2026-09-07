package com.ronbodnar.recipes.user;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.identity.domain.IdentityUser;
import com.ronbodnar.recipes.image.ImageServiceClient;
import com.ronbodnar.recipes.user.dto.UserAccountChangeRequest;
import com.ronbodnar.recipes.user.dto.UserAccountDTO;

import com.ronbodnar.recipes.user.dto.UserAccountSummaryDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class UserAccountService {

    private final ImageServiceClient imageServiceClient;

    private final UserAccountRepository userAccountRepository;

    public UserAccountService(ImageServiceClient imageServiceClient, UserAccountRepository userAccountRepository) {
        this.imageServiceClient = imageServiceClient;
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccountSummaryDTO getSummaryBySubject(String subject) {
        UserAccount userAccount = userAccountRepository.findByIdentityProviderSubject(subject)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserAccountSummaryDTO.fromEntity(userAccount);
    }

    public UserAccountDTO getOrCreateUserAccount(IdentityUser identityUser) {
        UserAccount userAccount = userAccountRepository.findByIdentityProviderSubject(identityUser.subject())
                .orElseGet(() -> {
                    String displayName = identityUser.familyName() + " " + identityUser.givenName();

                    UserAccount user = new UserAccount();
                    user.setIdentityProviderSubject(identityUser.subject());
                    user.setDisplayName(displayName.trim());

                    return userAccountRepository.save(user);
                });

        return UserAccountDTO.fromEntity(userAccount);
    }

    public void updateUserAccount(IdentityUser identityUser, UserAccountChangeRequest request) {
        log.info("Processing user account update request: {}", request);
        UserAccount userAccount = userAccountRepository.findByIdentityProviderSubject(identityUser.subject())
                .orElseThrow(() ->
                        new BusinessException(
                            ErrorCode.USER_NOT_FOUND,
                            "Failed to find a UserAccount with ID %s".formatted(identityUser.subject())
                        )
                );

        boolean isDisplayNameTaken = userAccountRepository.existsDisplayNameUsedByAnotherUser(
                request.displayName(),
                identityUser.subject()
        );

        if (isDisplayNameTaken) {
            throw new BusinessException(
                    ErrorCode.DISPLAY_NAME_ALREADY_IN_USE,
                    "displayName",
                    "This display name is already in use."
            );
        }

        UUID existingProfileImageId = userAccount.getProfileImageId();

        userAccount.setDisplayName(request.displayName());
        userAccount.setProfileImageId(request.profileImageId());

        if (!Objects.equals(existingProfileImageId, request.profileImageId())
                && existingProfileImageId != null) {
            imageServiceClient.markForDeletion(Set.of(existingProfileImageId));
        }

        if (userAccount.getProfileImageId() != null) {
            imageServiceClient.attach(Set.of(userAccount.getProfileImageId()));
        }

        log.info("Updated user account with ID: {}", userAccount.getId());
    }
}
