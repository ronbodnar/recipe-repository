package com.ronbodnar.recipes.modules.group.invitation;

import com.ronbodnar.recipes.modules.group.Group;
import com.ronbodnar.recipes.modules.identity.user.UserAccount;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_group_invitation")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class GroupInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_user_id", nullable = false)
    private UserAccount invitedUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by_user_id", nullable = false)
    private UserAccount invitedBy;

    @Column(name = "invite_code", nullable = false)
    private UUID inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GroupInvitationStatus status;

    @CreatedDate
    @Column(name = "invite_date", nullable = false, updatable = false)
    private LocalDateTime inviteDate;

}
