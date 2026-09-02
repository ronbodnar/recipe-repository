package com.ronbodnar.recipes.identity;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.identity.domain.IdentityUser;
import com.ronbodnar.recipes.user.dto.UserAccountChangeRequest;
import com.ronbodnar.recipes.identity.provider.IdentityProvider;
import com.ronbodnar.recipes.user.UserAccountService;
import com.ronbodnar.recipes.user.dto.UserAccountDTO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class IdentityService {

    private final IdentityProvider identityProvider;
    private final UserAccountService userAccountService;

    public IdentityService(IdentityProvider identityProvider, UserAccountService userAccountService) {
        this.identityProvider = identityProvider;
        this.userAccountService = userAccountService;
    }

    public UserAccountDTO getAuthenticatedUser(Jwt jwt) {
        IdentityUser identityUser = identityProvider.fromToken(jwt);

        return userAccountService.getOrCreateUserAccount(identityUser);
    }

    @Transactional(rollbackFor = BusinessException.class)
    public void updateAuthenticatedUser(UserAccountChangeRequest request, Jwt jwt) {
        log.info("Received request to update authenticated user: {}", request);

        IdentityUser identityUser = identityProvider.fromToken(jwt);
        userAccountService.updateUserAccount(identityUser, request);

        IdentityUser updatedUser = identityUser.withChanges(request);
        identityProvider.updateUser(updatedUser);
    }
}