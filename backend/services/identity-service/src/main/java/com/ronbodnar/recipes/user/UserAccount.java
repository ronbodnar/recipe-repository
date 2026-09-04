package com.ronbodnar.recipes.user;

import jakarta.persistence.*;

import jakarta.validation.constraints.Size;

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
     * The identity provider is the source of truth for this user's authentication
     * identity and identity attributes such as username, email, given name, and
     * family name. Changes to these attributes are propagated through
     * IdentityProvider rather than managed directly by this entity.
     */
    @Column(name = "idp_subject", nullable = false, unique = true)
    private String identityProviderSubject;

    @Size(min = 2, max = 50)
    @Column(name = "display_name", nullable = false, unique = true, length = 50)
    private String displayName;

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
        return String.format("User={id=%s, identityProviderSubject=%s, profileImageId=%s}",
                this.id, this.identityProviderSubject, this.profileImageId);
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