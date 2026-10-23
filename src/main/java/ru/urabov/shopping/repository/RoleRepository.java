package ru.urabov.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urabov.shopping.entity.Role;
import ru.urabov.shopping.entity.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
