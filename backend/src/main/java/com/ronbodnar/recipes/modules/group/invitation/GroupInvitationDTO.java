package com.ronbodnar.recipes.modules.group.invitation;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupInvitationDTO(
        UUID invitationCode,
        String invitedUsername,
        UUID invitedProfileImageId,
        String invitedByUsername,
        UUID invitedByProfileImageId,
        GroupInvitationStatus status,
        LocalDateTime invitedAt
) {

    public static GroupInvitationDTO from(GroupInvitation groupInvitation) {
        return new GroupInvitationDTO(
                groupInvitation.getId(),
                groupInvitation.getInvitedUser().getUsername(),
                groupInvitation.getInvitedUser().getProfileImageId(),
                groupInvitation.getInvitedBy().getUsername(),
                groupInvitation.getInvitedBy().getProfileImageId(),
                groupInvitation.getStatus(),
                groupInvitation.getInviteDate()
        );
    }
}
