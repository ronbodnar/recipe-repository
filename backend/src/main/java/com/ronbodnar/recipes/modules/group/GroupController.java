package com.ronbodnar.recipes.modules.group;

import com.ronbodnar.recipes.modules.group.domain.SearchQuery;
import com.ronbodnar.recipes.modules.group.dto.GroupSummaryDTO;
import com.ronbodnar.recipes.modules.group.dto.GroupRequest;
import com.ronbodnar.recipes.modules.group.invitation.GroupInvitationDTO;
import com.ronbodnar.recipes.modules.group.member.dto.GroupMemberDTO;
import com.ronbodnar.recipes.modules.group.member.dto.InviteGroupMemberRequest;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public Page<GroupSummaryDTO> getGroups(
            SearchQuery searchQuery,
            Pageable pageable,
            @AuthenticationPrincipal SecurityUserDetails securityUser
    ) {
        return groupService.getGroups(securityUser.getId(), searchQuery, pageable);
    }

    // Add Groups
    @PostMapping
    public GroupSummaryDTO create(
            @RequestBody GroupRequest request,
            @AuthenticationPrincipal SecurityUserDetails securityUser
    ) {
        return groupService.createGroup(request, securityUser.getId());
    }

    @PutMapping("/{groupId}")
    public GroupSummaryDTO update(
            @PathVariable Long groupId,
            @RequestBody GroupRequest request,
            @AuthenticationPrincipal SecurityUserDetails securityUser
    ) {
        return groupService.updateGroup(groupId, request, securityUser.getId());
    }

    @GetMapping("/{groupId}")
    public GroupSummaryDTO getGroup(@PathVariable Long groupId) {
        return groupService.getById(groupId);
    }

    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable Long groupId, @AuthenticationPrincipal SecurityUserDetails securityUser) {
        groupService.deleteGroup(groupId, securityUser.getId());
    }

    @GetMapping("/{groupId}/members")
    public Page<GroupMemberDTO> getGroupMembers(@PathVariable Long groupId, Pageable pageable) {
        return groupService.getMembersById(groupId, pageable);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public void removeGroupMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        groupService.removeGroupMember(groupId, memberId);
    }

    @GetMapping("/{groupId}/invitations")
    public Page<GroupInvitationDTO> getGroupInvitations(@PathVariable Long groupId, Pageable pageable) {
        return groupService.getInvitationsById(groupId, pageable);
    }

    @PostMapping("/{groupId}/invitations")
    public void inviteGroupMember(
            @PathVariable Long groupId,
            @RequestBody InviteGroupMemberRequest inviteGroupMemberRequest,
            @AuthenticationPrincipal SecurityUserDetails securityUser
    ) {
        groupService.inviteGroupMember(groupId, securityUser.getId(), inviteGroupMemberRequest.usernameOrEmail());
    }

    @PostMapping("/{groupId}/invitations/{invitationId}/cancel")
    public void cancelInvitation(
            @PathVariable UUID invitationId,
            @AuthenticationPrincipal SecurityUserDetails securityUser
    ) {
        groupService.cancelInvitation(invitationId, securityUser.getId());
    }

    @PostMapping("/{groupId}/invitations/{invitationId}/{response}")
    public void respondToInvitation(
            @PathVariable UUID invitationId,
            @PathVariable Boolean response,
            @AuthenticationPrincipal SecurityUserDetails securityUser
    ) {
        groupService.respondToInvitation(invitationId, response, securityUser.getId());
    }

}