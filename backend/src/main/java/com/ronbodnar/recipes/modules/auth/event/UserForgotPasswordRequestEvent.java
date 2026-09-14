package com.ronbodnar.recipes.modules.auth.event;

public record UserForgotPasswordRequestEvent(String email, String username, String token) {}