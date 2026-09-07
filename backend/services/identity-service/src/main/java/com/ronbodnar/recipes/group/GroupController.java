package com.ronbodnar.recipes.group;

import com.ronbodnar.recipes.group.domain.SearchQuery;
import com.ronbodnar.recipes.group.dto.GroupDTO;
import com.ronbodnar.recipes.group.dto.GroupRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/identity/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public Page<GroupDTO> getGroups(SearchQuery searchQuery, Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        return groupService.getGroups(searchQuery, pageable);
    }

    // Add Groups
    @PostMapping
    public GroupDTO create(@RequestBody GroupRequest request) {
        return groupService.createGroup(request);
    }

    // Update Groups
    // Remove Groups

    // Add Group Member
    // Change Group Member Role
    // Delete Group Member

    // Check Group Membership
}
