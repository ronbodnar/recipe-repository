package com.ronbodnar.recipes.group;

import com.ronbodnar.recipes.group.dto.GroupDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("""
        SELECT g
        FROM Group g
        WHERE g.name LIKE CONCAT('%', :searchValue, '%')
    """)
    Page<GroupDTO> getGroupsWithNameLike(String searchValue, Pageable pageable);
}
