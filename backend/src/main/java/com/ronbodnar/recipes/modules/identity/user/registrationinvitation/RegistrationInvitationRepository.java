package com.ronbodnar.recipes.modules.identity.user.registrationinvitation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegistrationInvitationRepository extends JpaRepository<RegistrationInvitation, Long> {

    Optional<RegistrationInvitation> findByRegistrationCode(UUID registrationCode);
}