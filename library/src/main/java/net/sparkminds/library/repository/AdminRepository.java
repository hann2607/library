package net.sparkminds.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long>{

}
