package com.ronbodnar.recipes.modules.group.event;

import com.ronbodnar.recipes.modules.group.Group;
import com.ronbodnar.recipes.modules.identity.user.UserAccount;

public record GroupMemberInviteEvent(Group group, UserAccount invitedUser) {}