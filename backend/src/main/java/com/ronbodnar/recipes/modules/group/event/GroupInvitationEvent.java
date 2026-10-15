package com.ronbodnar.recipes.modules.group.event;

import java.util.UUID;

public record GroupInvitationEvent(
        String email,
        String groupName,
        String inviterName,
        UUID token
) {}