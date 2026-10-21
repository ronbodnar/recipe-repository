package com.ronbodnar.recipes.modules.group.event;

import java.util.UUID;

public record GroupInvitationEvent(
        String email,
        Long groupId,
        String groupName,
        String inviterName,
        UUID token
) {}