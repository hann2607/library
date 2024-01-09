package net.sparkminds.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByEmail(String email);
}
