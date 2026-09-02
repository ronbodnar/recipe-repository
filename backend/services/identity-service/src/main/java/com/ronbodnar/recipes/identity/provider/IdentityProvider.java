package com.ronbodnar.recipes.identity.provider;

import com.ronbodnar.recipes.identity.domain.IdentityUser;

import org.springframework.security.oauth2.jwt.Jwt;

public interface IdentityProvider {

    IdentityUser fromToken(Jwt jwt);

    void updateUser(IdentityUser updatedUser);

}