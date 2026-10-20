package com.ronbodnar.recipes.modules.group;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.group.domain.GroupRole;
import com.ronbodnar.recipes.modules.group.domain.SearchQuery;
import com.ronbodnar.recipes.modules.group.dto.GroupSummaryDTO;
import com.ronbodnar.recipes.modules.group.dto.GroupRequest;
import com.ronbodnar.recipes.modules.group.event.GroupInvitationEvent;
import com.ronbodnar.recipes.modules.group.invitation.GroupInvitation;
import com.ronbodnar.recipes.modules.group.invitation.GroupInvitationDTO;
import com.ronbodnar.recipes.modules.group.invitation.GroupInvitationRepository;
import com.ronbodnar.recipes.modules.group.invitation.GroupInvitationStatus;
import com.ronbodnar.recipes.modules.group.member.GroupMember;
import com.ronbodnar.recipes.modules.group.member.GroupMemberRepository;
import com.ronbodnar.recipes.modules.group.member.dto.GroupMemberDTO;
import com.ronbodnar.recipes.modules.identity.user.UserAccount;
import com.ronbodnar.recipes.modules.identity.user.UserAccountService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInvitationRepository groupInvitationRepository;

    private final UserAccountService userAccountService;

    private final ApplicationEventPublisher eventPublisher;

    public GroupService(
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            GroupInvitationRepository groupInvitationRepository,
            UserAccountService userAccountService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupInvitationRepository = groupInvitationRepository;
        this.userAccountService = userAccountService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public GroupSummaryDTO getById(Long id) {
        Group group = groupRepository.findById(id).orElseThrow(() ->
                new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        return GroupSummaryDTO.from(group);
    }

    @Transactional(readOnly = true)
    public Page<GroupMemberDTO> getMembersById(Long groupId, Pageable pageable) {
        return groupMemberRepository.findByGroupId(groupId, pageable).map(GroupMemberDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<GroupInvitationDTO> getInvitationsById(Long groupId, Pageable pageable) {
        return groupInvitationRepository.findByGroupId(groupId, pageable).map(GroupInvitationDTO::from);
    }

    public Page<GroupSummaryDTO> getGroups(
            Long userId,
            SearchQuery searchQuery,
            Pageable pageable
    ) {
        String searchValue = searchQuery.searchValue() == null ? "" : searchQuery.searchValue();

        // Member order is a collection count and not a sortable properly.
        // Separating the repository functions allows for hardcoding sorting.
        Sort.Order memberOrder = pageable.getSort().getOrderFor("memberCount");
        if (memberOrder != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            if (memberOrder.isAscending()) {
                return groupRepository.getGroupsWithNameLikeOrderByMemberCountAsc(userId, searchValue, pageable);
            } else {
                return groupRepository.getGroupsWithNameLikeOrderByMemberCountDesc(userId, searchValue, pageable);
            }
        }

        return groupRepository.getGroupsWithNameLike(userId, searchValue, pageable);
    }

    @Transactional
    public GroupSummaryDTO createGroup(GroupRequest request, Long ownerId) {
        if (groupRepository.existsByName(request.name())) {
            throw new BusinessException(
                    ErrorCode.GROUP_NAME_ALREADY_IN_USE,
                    "name",
                    "A group with the same name already exists."
            );
        }

        Group group = new Group();
        group.setName(request.name());
        group.setDescription(request.description());

        UserAccount owner = userAccountService.getById(ownerId);

        GroupMember ownerMember = new GroupMember();
        ownerMember.setUserAccount(owner);
        ownerMember.setRole(GroupRole.OWNER);

        group.addMember(ownerMember);

        Group saved = groupRepository.save(group);

        return GroupSummaryDTO.from(saved);
    }

    @Transactional
    public GroupSummaryDTO updateGroup(Long groupId, GroupRequest request, Long ownerId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        boolean exists = groupRepository.existsByNameAndIdIsNot(request.name(), groupId);
        if (exists) {
            throw new BusinessException(ErrorCode.GROUP_NAME_ALREADY_IN_USE);
        }

        if (!group.getOwnerMember().getUserAccount().getId().equals(ownerId)) {
            log.info("Not group owner. Group owner id: " + group.getOwnerMember().getId() + ". owner id: " + ownerId);
            throw new BusinessException(ErrorCode.NOT_GROUP_OWNER);
        }

        group.setName(request.name());
        group.setDescription(request.description());

        return GroupSummaryDTO.from(groupRepository.save(group));
    }

    @Transactional
    public void deleteGroup(Long groupId, Long ownerId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        if (!group.getOwnerMember().getUserAccount().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.NOT_GROUP_OWNER);
        }

        groupRepository.delete(group);
    }

    @Transactional
    public void inviteGroupMember(Long groupId, Long inviterId, String usernameOrEmail) {
        UserAccount invitedUser = userAccountService.getByUsernameOrEmailSilent(usernameOrEmail);
        if (invitedUser == null) {
            log.info("Failed to find a user for group invite with email or username: {}", usernameOrEmail);
            return;
        }

        UserAccount inviter = userAccountService.getById(inviterId);

        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        // Avoid sending duplicate invitations when the user is already a member
        if (group.isMember(invitedUser.getId())) {
            log.info("Invited user is already part of the same group.");
            return;
        }

        String inviterName = inviter.getFullName() == null ? inviter.getUsername() : inviter.getFullName();
        UUID invitationToken = UUID.randomUUID();

        GroupInvitation invitation = new GroupInvitation();
        invitation.setGroup(group);
        invitation.setInvitedBy(inviter);
        invitation.setInvitedUser(invitedUser);
        invitation.setId(invitationToken);
        invitation.setStatus(GroupInvitationStatus.PENDING);

        groupInvitationRepository.saveAndFlush(invitation);

        eventPublisher.publishEvent(
                new GroupInvitationEvent(invitedUser.getEmail(), group.getId(), group.getName(), inviterName, invitationToken)
        );
    }

    @Transactional
    public void respondToInvite(UUID inviteCode, boolean accepted, Long authUserId) {
        GroupInvitation invitation = groupInvitationRepository.findById(inviteCode).orElseThrow(() ->
                new BusinessException(ErrorCode.INVALID_GROUP_INVITE_CODE));

        if (!authUserId.equals(invitation.getInvitedUser().getId())) {
            throw new BusinessException(ErrorCode.INVALID_GROUP_INVITE_CODE);
        }

        if (invitation.getStatus() != GroupInvitationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_GROUP_INVITE_CODE);
        }

        invitation.setStatus(accepted ? GroupInvitationStatus.ACCEPTED : GroupInvitationStatus.DECLINED);

        if (accepted) {
            GroupMember member = new GroupMember();
            member.setUserAccount(invitation.getInvitedUser());
            member.setRole(GroupRole.MEMBER);
            invitation.getGroup().addMember(member);
        }
    }

    public void cancelInvite(UUID inviteCode, Long authUserId) {
        GroupInvitation invitation = groupInvitationRepository.findById(inviteCode).orElseThrow(() ->
                new BusinessException(ErrorCode.INVALID_GROUP_INVITE_CODE));

        if (!authUserId.equals(invitation.getInvitedBy().getId())
                || !invitation.getGroup().getOwnerMember().getUserAccount().getId().equals(authUserId)) {
            throw new BusinessException(ErrorCode.INVALID_GROUP_INVITE_CODE);
        }

        invitation.setStatus(GroupInvitationStatus.CANCELLED);
    }

    @Transactional
    public void removeGroupMember(Long groupId, Long memberId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        GroupMember member = groupMemberRepository.findById(memberId).orElseThrow(() ->
                new BusinessException(ErrorCode.GROUP_MEMBER_NOT_FOUND));

        group.getMembers().remove(member);
        groupMemberRepository.delete(member);
    }
}
