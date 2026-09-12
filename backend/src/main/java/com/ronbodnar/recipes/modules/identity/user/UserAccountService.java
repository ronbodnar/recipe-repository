package com.ronbodnar.recipes.modules.identity.user;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.exception.payload.FieldError;
import com.ronbodnar.recipes.modules.identity.role.RoleService;
import com.ronbodnar.recipes.modules.auth.dto.RegisterRequest;
import com.ronbodnar.recipes.modules.identity.user.dto.UserAccountChangeRequest;
import com.ronbodnar.recipes.modules.identity.user.dto.UserAccountSummaryDTO;
import com.ronbodnar.recipes.modules.image.ImageService;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetails;

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

    private final UserAccountRepository repository;

    public UserAccountService(RoleService roleService, PasswordEncoder passwordEncoder, ImageService imageService, UserAccountRepository repository) {
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.imageService = imageService;
        this.repository = repository;
    }

    public UserAccount getById(Long id) {
        return repository.findByIdWithRoles(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public UserAccountSummaryDTO getSummaryById(Long id) {
        UserAccount userAccount = repository.findByIdWithRoles(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserAccountSummaryDTO.from(userAccount);
    }

    public void updateUserAccount(UserAccountChangeRequest request, SecurityUserDetails securityUser) {
        log.info("Processing user account update request: {}", request);
        UserAccount userAccount = repository.findById(securityUser.getId())
                .orElseThrow(() ->
                        new BusinessException(
                            ErrorCode.USER_NOT_FOUND,
                            "Failed to find a UserAccount with ID %s".formatted(securityUser.getId())
                        )
                );

        List<FieldError> fieldErrorList = new ArrayList<>();

        boolean isUsernameTaken = repository.existsUsernameByAnotherUser(request.username(), securityUser.getId());
        if (isUsernameTaken) {
            fieldErrorList.add(
                    new FieldError(
                            ErrorCode.USERNAME_ALREADY_IN_USE,
                            "username",
                            "Username has already been registered."
                    )
            );
        }

        boolean isEmailTaken = repository.existsEmailByAnotherUser(request.email(), securityUser.getId());
        if (isEmailTaken) {
            fieldErrorList.add(
                    new FieldError(
                            ErrorCode.EMAIL_ALREADY_IN_USE,
                            "email",
                            "E-mail address has already been registered."
                    )
            );
        }

        if (!fieldErrorList.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, fieldErrorList);
        }

        UUID existingProfileImageId = userAccount.getProfileImageId();

        userAccount.setUsername(request.username());
        userAccount.setEmail(request.email());
        userAccount.setGivenName(request.givenName());
        userAccount.setFamilyName(request.familyName());
        userAccount.setProfileImageId(request.profileImageId());

        repository.saveAndFlush(userAccount);

        if (!Objects.equals(existingProfileImageId, request.profileImageId())
                && existingProfileImageId != null) {
            imageService.markForDeletion(List.of(existingProfileImageId));
        }

        if (userAccount.getProfileImageId() != null) {
            imageService.attach(List.of(userAccount.getProfileImageId()));
        }

        log.info("Updated user account with ID: {}", userAccount.getId());
    }

    public UserAccount create(RegisterRequest registerRequest) {
        List<FieldError> fieldErrorList = new ArrayList<>();
        if (repository.existsByUsername(registerRequest.username())) {
            fieldErrorList.add(new FieldError(ErrorCode.USERNAME_ALREADY_IN_USE, "username", "Username has already been registered."));
        }

        if (repository.existsByEmail(registerRequest.email())) {
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
        userAccount.setRoles(Set.of(roleService.findByName("USER")));

        return repository.save(userAccount);
    }
}
