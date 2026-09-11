package com.ronbodnar.recipes.modules.identity.user;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.exception.payload.FieldError;
import com.ronbodnar.recipes.modules.identity.role.RoleService;
import com.ronbodnar.recipes.modules.auth.dto.RegisterRequest;
import com.ronbodnar.recipes.modules.identity.user.dto.UserAccountSummaryDTO;
import com.ronbodnar.recipes.modules.image.ImageService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserAccountService {

    private final RoleService roleService;
    private final ImageService imageService;

    private final PasswordEncoder passwordEncoder;

    private final UserAccountRepository userAccountRepository;

    public UserAccountService(RoleService roleService, PasswordEncoder passwordEncoder, ImageService imageService, UserAccountRepository userAccountRepository) {
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.imageService = imageService;
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount getById(Long id) {
        return userAccountRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public UserAccountSummaryDTO getSummaryById(Long id) {
        UserAccount userAccount = userAccountRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserAccountSummaryDTO.from(userAccount);
    }

/*    public void updateUserAccount(IdentityUser identityUser, UserAccountChangeRequest request) {
        log.info("Processing user account update request: {}", request);
        UserAccount userAccount = userAccountRepository.findByIdentityProviderSubject(identityUser.subject())
                .orElseThrow(() ->
                        new BusinessException(
                            ErrorCode.USER_NOT_FOUND,
                            "Failed to find a UserAccount with ID %s".formatted(identityUser.subject())
                        )
                );

        boolean isUsernameTaken = userAccountRepository.existsDisplayNameUsedByAnotherUser(
                request.username(),
                identityUser.subject()
        );

        if (isUsernameTaken) {
            throw new BusinessException(
                    ErrorCode.DISPLAY_NAME_ALREADY_IN_USE,
                    "username",
                    "This display name is already in use."
            );
        }

        UUID existingProfileImageId = userAccount.getProfileImageId();

        userAccount.setUsername(request.username());
        userAccount.setProfileImageId(request.profileImageId());

        if (!Objects.equals(existingProfileImageId, request.profileImageId())
                && existingProfileImageId != null) {
            imageService.markForDeletion(List.of(existingProfileImageId));
        }

        if (userAccount.getProfileImageId() != null) {
            imageService.attach(List.of(userAccount.getProfileImageId()));
        }

        log.info("Updated user account with ID: {}", userAccount.getId());
    }*/

    public UserAccount create(RegisterRequest registerRequest) {
        List<FieldError> fieldErrorList = new ArrayList<>();
        if (userAccountRepository.existsByUsername(registerRequest.username())) {
            fieldErrorList.add(new FieldError(ErrorCode.USERNAME_ALREADY_IN_USE, "username", "Username has already been registered."));
        }

        if (userAccountRepository.existsByEmail(registerRequest.email())) {
            fieldErrorList.add(new FieldError(ErrorCode.EMAIL_ALREADY_IN_USE, "email", "E-mail address has already been registered."));
        }

        if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            fieldErrorList.add(new FieldError(ErrorCode.PASSWORD_MISMATCH, "password", "Password fields do not match."));
            fieldErrorList.add(new FieldError(ErrorCode.PASSWORD_MISMATCH, "confirmPassword", "Password fields do not match."));
        }

        if (!fieldErrorList.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, fieldErrorList);
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.password());

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(registerRequest.username());
        userAccount.setPassword(encodedPassword);
        userAccount.setEmail(registerRequest.email());
        userAccount.setGivenName(registerRequest.givenName());
        userAccount.setFamilyName(registerRequest.familyName());
        userAccount.setRoles(Set.of(roleService.getDefaultRole()));

        return userAccountRepository.save(userAccount);
    }
}
