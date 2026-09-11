package com.ronbodnar.recipes.modules.auth.domain;

public record TokenPair(String accessToken, String refreshToken) {}
