package com.ronbodnar.recipes.modules.group.invitation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, UUID> {

    Optional<GroupInvitation> findByInviteCode(UUID inviteCode);

}