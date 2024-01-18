package net.sparkminds.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

	Optional<Account> findByEmail(String email);
	
}
