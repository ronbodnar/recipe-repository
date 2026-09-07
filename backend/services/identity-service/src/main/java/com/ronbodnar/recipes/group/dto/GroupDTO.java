package com.ronbodnar.recipes.group.dto;

import java.time.LocalDateTime;

public record GroupDTO(String name, long memberCount, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
}
