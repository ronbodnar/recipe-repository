package com.ronbodnar.recipes.modules.identity.role;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final String defaultRoleName;

    private final RoleRepository roleRepository;

    public RoleService(@Value("${app.roles.default}") String defaultRoleName, RoleRepository roleRepository) {
        this.defaultRoleName = defaultRoleName;
        this.roleRepository = roleRepository;
    }

    public Role getDefaultRole() {
        return roleRepository.findByName(defaultRoleName).orElse(null);
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

}
