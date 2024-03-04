package com.ronbodnar.reciperepository.repository.user;

import com.ronbodnar.reciperepository.model.user.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByRoleType(Role.RoleType roleType);
}
