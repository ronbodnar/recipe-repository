package com.ronbodnar.recipes.modules.group;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.group.domain.GroupRole;
import com.ronbodnar.recipes.modules.group.domain.SearchQuery;
import com.ronbodnar.recipes.modules.group.dto.GroupSummaryDTO;
import com.ronbodnar.recipes.modules.group.dto.GroupRequest;
import com.ronbodnar.recipes.modules.group.member.GroupMember;
import com.ronbodnar.recipes.modules.group.member.GroupMemberRepository;
import com.ronbodnar.recipes.modules.group.member.dto.GroupMemberDTO;
import com.ronbodnar.recipes.modules.identity.user.UserAccount;
import com.ronbodnar.recipes.modules.identity.user.UserAccountService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    private final UserAccountService userAccountService;

    public GroupService(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository, UserAccountService userAccountService) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userAccountService = userAccountService;
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

    public Page<GroupSummaryDTO> getGroups(
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
                return groupRepository.getGroupsWithNameLikeOrderByMemberCountAsc(searchValue, pageable);
            } else {
                return groupRepository.getGroupsWithNameLikeOrderByMemberCountDesc(searchValue, pageable);
            }
        }

        return groupRepository.getGroupsWithNameLike(searchValue, pageable);
    }

    @Transactional
    public GroupSummaryDTO createGroup(GroupRequest request, Long ownerId) {
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
}
