package net.sparkminds.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Role;
import net.sparkminds.library.enumration.EnumRole;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByRole(EnumRole roleName);
}
