package com.ronbodnar.recipes.modules.auth.event;

public record UserRegisteredEvent(String username, String email) {}
