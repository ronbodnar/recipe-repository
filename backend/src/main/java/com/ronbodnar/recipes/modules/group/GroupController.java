package com.ronbodnar.recipes.modules.group;

import com.ronbodnar.recipes.modules.group.domain.SearchQuery;
import com.ronbodnar.recipes.modules.group.dto.GroupSummaryDTO;
import com.ronbodnar.recipes.modules.group.dto.GroupRequest;
import com.ronbodnar.recipes.modules.group.invitation.GroupInvitation;
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
            @AuthenticationPrincipal SecurityUserDetails securityUser) {
        return groupService.getGroups(securityUser.getId(), searchQuery, pageable);
    }

    // Add Groups
    @PostMapping
    public GroupSummaryDTO create(@RequestBody GroupRequest request, @AuthenticationPrincipal SecurityUserDetails securityUser) {
        return groupService.createGroup(request, securityUser.getId());
    }

    @PutMapping("/{id}")
    public GroupSummaryDTO update(@PathVariable Long id, @RequestBody GroupRequest request, @AuthenticationPrincipal SecurityUserDetails securityUser) {
        return groupService.updateGroup(id, request, securityUser.getId());
    }

    @GetMapping("/{id}")
    public GroupSummaryDTO getGroup(@PathVariable Long id) {
        return groupService.getById(id);
    }

    @GetMapping("/{id}/members")
    public Page<GroupMemberDTO> getGroupMembers(@PathVariable Long id, Pageable pageable) {
        return groupService.getMembersById(id, pageable);
    }

    @GetMapping("/{id}/invitations")
    public Page<GroupInvitationDTO> getGroupInvitations(@PathVariable Long id, Pageable pageable) {
        return groupService.getInvitationsById(id, pageable);
    }

    @PostMapping("/{groupId}/members/invite")
    public void inviteGroupMember(
            @PathVariable Long groupId,
            @RequestBody InviteGroupMemberRequest inviteGroupMemberRequest,
            @AuthenticationPrincipal SecurityUserDetails securityUser) {
        groupService.inviteGroupMember(groupId, securityUser.getId(), inviteGroupMemberRequest.usernameOrEmail());
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public void removeGroupMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        groupService.removeGroupMember(groupId, memberId);
    }

    @PostMapping("/{groupId}/invitations/{inviteCode}/{response}")
    public void respondToInvite(
            @PathVariable UUID inviteCode,
            @PathVariable Boolean response,
            @AuthenticationPrincipal SecurityUserDetails securityUser
    ) {
        groupService.respondToInvite(inviteCode, response, securityUser.getId());
    }

    @PostMapping("/{groupId}/invitations/{inviteCode}/cancel")
    public void cancelInvite(@PathVariable UUID inviteCode, @AuthenticationPrincipal SecurityUserDetails securityUser) {
        groupService.cancelInvite(inviteCode, securityUser.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id, @AuthenticationPrincipal SecurityUserDetails securityUser) {
        groupService.deleteGroup(id, securityUser.getId());
    }

}