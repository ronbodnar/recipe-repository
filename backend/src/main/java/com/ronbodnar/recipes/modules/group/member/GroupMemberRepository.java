package com.ronbodnar.recipes.modules.group.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Page<GroupMember> findByGroupId(Long groupId, Pageable pageable);

}