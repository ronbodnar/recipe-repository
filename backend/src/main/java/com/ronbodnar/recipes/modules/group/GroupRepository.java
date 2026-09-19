package com.ronbodnar.recipes.modules.group;

import com.ronbodnar.recipes.modules.group.dto.GroupSummaryDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByNameAndIdIsNot(String name, Long id);

    @Query("""
        SELECT new com.ronbodnar.recipes.modules.group.dto.GroupSummaryDTO(
            g.id,
            (SELECT m.id FROM g.members m WHERE m.role = 'OWNER'),
            g.name,
            g.description,
            SIZE(g.members),
            g.createdAt,
            g.lastModifiedAt
        )
        FROM Group g
        WHERE g.name LIKE CONCAT('%', :searchValue, '%')
    """)
    Page<GroupSummaryDTO> getGroupsWithNameLike(
            String searchValue,
            Pageable pageable
    );

    @Query("""
        SELECT new com.ronbodnar.recipes.modules.group.dto.GroupSummaryDTO(
            g.id,
            (SELECT m.id FROM g.members m WHERE m.role = 'OWNER'),
            g.name,
            g.description,
            SIZE(g.members),
            g.createdAt,
            g.lastModifiedAt
        )
        FROM Group g
        WHERE g.name LIKE CONCAT('%', :searchValue, '%')
        ORDER BY SIZE(g.members) ASC
    """)
    Page<GroupSummaryDTO> getGroupsWithNameLikeOrderByMemberCountAsc(
            String searchValue,
            Pageable pageable
    );

    @Query("""
        SELECT new com.ronbodnar.recipes.modules.group.dto.GroupSummaryDTO(
            g.id,
            (SELECT m.id FROM g.members m WHERE m.role = 'OWNER'),
            g.name,
            g.description,
            SIZE(g.members),
            g.createdAt,
            g.lastModifiedAt
        )
        FROM Group g
        WHERE g.name LIKE CONCAT('%', :searchValue, '%')
        ORDER BY SIZE(g.members) DESC
    """)
    Page<GroupSummaryDTO> getGroupsWithNameLikeOrderByMemberCountDesc(
            String searchValue,
            Pageable pageable
    );
}
