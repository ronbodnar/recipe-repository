package com.ronbodnar.recipes.modules.identity.user;

import com.ronbodnar.recipes.modules.identity.group.member.GroupMember;
import com.ronbodnar.recipes.modules.identity.role.Role;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

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

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "given_name", length = 30)
    private String givenName;

    @Column(name = "family_name", length = 30)
    private String familyName;

    @Column(name = "profile_image_id")
    private UUID profileImageId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "userAccount")
    private List<GroupMember> groupMemberships = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("User(id=%s, username=%s, givenName=%s, familyName=%s, email=%s)",
                this.id, this.username, this.givenName, this.familyName, this.email);
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