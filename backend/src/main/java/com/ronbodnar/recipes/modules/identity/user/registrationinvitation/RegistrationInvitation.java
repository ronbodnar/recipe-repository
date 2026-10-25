package com.ronbodnar.recipes.modules.identity.user.registrationinvitation;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "registration_invitation")
@NoArgsConstructor
public class RegistrationInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "registration_code", nullable = false)
    private UUID registrationCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RegistrationInvitationStatus status;

}