package com.ronbodnar.recipes.modules.group.invitation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, UUID> {

    @Query("""
        SELECT g FROM GroupInvitation g WHERE g.group.id = :groupId AND g.status = 'PENDING'
    """)
    Page<GroupInvitation> findByGroupId(Long groupId, Pageable pageable);

    @Query("""
        SELECT COUNT(g.id) > 0
        FROM GroupInvitation g
        WHERE g.invitedUser.email = :email AND g.status = 'PENDING'
    """)
    boolean hasPendingInvitation(String email);

}