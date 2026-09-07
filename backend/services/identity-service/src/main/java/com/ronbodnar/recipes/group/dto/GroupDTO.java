package com.ronbodnar.recipes.group.dto;

import java.time.LocalDateTime;

public record GroupDTO(String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
