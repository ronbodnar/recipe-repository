package com.ronbodnar.recipes.modules.group.member.dto;

import com.ronbodnar.recipes.modules.group.domain.GroupRole;
import com.ronbodnar.recipes.modules.group.member.GroupMember;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupMemberDTO(
        Long id,
        Long userId,
        String username,
        String fullName,
        UUID profileImageId,
        GroupRole role,
        LocalDateTime joinedAt
) {

    public static GroupMemberDTO from(GroupMember groupMember) {
        String fullName = "";
        if (groupMember.getUserAccount().getGivenName() != null) {
            fullName = groupMember.getUserAccount().getGivenName();
        }
        if (groupMember.getUserAccount().getFamilyName() != null) {
            fullName += " " + groupMember.getUserAccount().getFamilyName();
        }

        return new GroupMemberDTO(
                groupMember.getId(),
                groupMember.getUserAccount().getId(),
                groupMember.getUserAccount().getUsername(),
                fullName.trim(),
                groupMember.getUserAccount().getProfileImageId(),
                groupMember.getRole(),
                groupMember.getJoinedAt()
        );
    }
}