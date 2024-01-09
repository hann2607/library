package net.sparkminds.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

	List<Account> findByEmail(String email);
	
}
