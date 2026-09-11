package com.ronbodnar.recipes.modules.identity.group.member;

import com.ronbodnar.recipes.modules.identity.group.Group;
import com.ronbodnar.recipes.modules.identity.group.domain.GroupRole;
import com.ronbodnar.recipes.modules.identity.user.UserAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "user_group_member",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_group_member_group_user",
                columnNames = {"group_id", "user_account_id"}
        )
)
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private GroupRole role;

}