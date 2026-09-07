package com.ronbodnar.recipes.group;

import com.ronbodnar.recipes.group.dto.GroupDTO;
import com.ronbodnar.recipes.group.dto.GroupRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Page<GroupDTO> getGroups(Pageable pageable) {
        Page<Group> groups = groupRepository.findAll(pageable);

        return groups.map(group -> new GroupDTO(group.getName(), group.getCreatedAt(), group.getLastModifiedAt()));
    }

    public GroupDTO createGroup(GroupRequest request) {
        Group group = new Group();
        group.setName(request.name());

        Group saved = groupRepository.save(group);

        return new GroupDTO(saved.getName(), saved.getCreatedAt(), saved.getLastModifiedAt());
    }

}
