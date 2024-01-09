package net.sparkminds.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
