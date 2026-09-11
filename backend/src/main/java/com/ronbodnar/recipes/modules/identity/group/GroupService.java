package com.ronbodnar.recipes.modules.identity.group;

import com.ronbodnar.recipes.modules.identity.group.domain.SearchQuery;
import com.ronbodnar.recipes.modules.identity.group.dto.GroupDTO;
import com.ronbodnar.recipes.modules.identity.group.dto.GroupRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Page<GroupDTO> getGroups(
            SearchQuery searchQuery,
            Pageable pageable
    ) {
        String searchValue = searchQuery.searchValue() == null ? "" : searchQuery.searchValue();
        System.out.println("searchValue: " + searchValue);
        return groupRepository.getGroupsWithNameLike(searchValue, pageable);
    }

    public GroupDTO createGroup(GroupRequest request) {
        Group group = new Group();
        group.setName(request.name());

        Group saved = groupRepository.save(group);

        return new GroupDTO(saved.getName(), 1, saved.getCreatedAt(), saved.getLastModifiedAt());
    }

}
