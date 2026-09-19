package com.ronbodnar.recipes.modules.group.dto;

import com.ronbodnar.recipes.modules.group.Group;

import java.time.LocalDateTime;

public record GroupSummaryDTO(Long id, Long ownerId, String name, String description, long memberCount, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {

    public static GroupSummaryDTO from(Group group) {
        return new GroupSummaryDTO(
                group.getId(),
                group.getOwnerMember().getUserAccount().getId(),
                group.getName(),
                group.getDescription(),
                group.getMembers().size(),
                group.getCreatedAt(),
                group.getLastModifiedAt()
        );
    }
}
