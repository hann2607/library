package net.sparkminds.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

	Account findByEmail(String email);
	
}
