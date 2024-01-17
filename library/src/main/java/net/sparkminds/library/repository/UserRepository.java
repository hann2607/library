package net.sparkminds.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Customer;

public interface UserRepository extends JpaRepository<Customer, Long>{
	Optional<Customer> findByEmail(String email);
}
