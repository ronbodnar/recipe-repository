package com.ronbodnar.recipes.user;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user_account")
@EntityListeners(AuditingEntityListener.class)
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Keycloak acts as the source of truth for this UserAccount's authentication and metadata.
     * It's responsible for the username, email, and user's given and family name. Updates to these fields
     * are made to Keycloak through {TBD} and to avoid synchronization issues are solely kept there.
     */
    @Column(name = "keycloak_subject", nullable = false, unique = true)
    private UUID keycloakSubject;

    @Column(name = "profile_image_id")
    private UUID profileImageId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    @Override
    public String toString() {
        return String.format("User={id=%s, keycloakSubject=%s, profileImageId=%s}",
                this.id, this.keycloakSubject, this.profileImageId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount userAccount)) return false;
        return id != null && id.equals(userAccount.id);
    }

    public int hashCode() {
        return getClass().hashCode();
    }

}