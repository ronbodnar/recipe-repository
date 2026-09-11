package com.ronbodnar.recipes.modules.auth.domain;

import com.ronbodnar.recipes.modules.identity.user.UserAccount;

public record AuthenticationResponse(UserAccount userAccount, TokenPair tokenPair) {}
