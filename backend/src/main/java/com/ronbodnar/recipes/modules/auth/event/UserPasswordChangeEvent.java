package com.ronbodnar.recipes.modules.auth.event;

public record UserPasswordChangeEvent(String email, String username) {}