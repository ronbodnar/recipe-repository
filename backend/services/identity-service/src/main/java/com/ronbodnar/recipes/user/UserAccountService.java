package com.ronbodnar.recipes.user;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.identity.domain.IdentityUser;
import com.ronbodnar.recipes.user.dto.UserAccountChangeRequest;
import com.ronbodnar.recipes.user.dto.UserAccountDTO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccountDTO getOrCreateUserAccount(IdentityUser identityUser) {
        UserAccount userAccount = userAccountRepository.findByIdentityProviderSubject(identityUser.subject())
                .orElseGet(() -> {
                    UserAccount user = new UserAccount();
                    user.setIdentityProviderSubject(identityUser.subject());
                    user.setDisplayName(identityUser.username());

                    return userAccountRepository.save(user);
                });

        return UserAccountDTO.fromEntity(userAccount);
    }

    public void updateUserAccount(IdentityUser identityUser, UserAccountChangeRequest request) {
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

        userAccount.setDisplayName(request.displayName());

        // Updating profile image
    }
}
