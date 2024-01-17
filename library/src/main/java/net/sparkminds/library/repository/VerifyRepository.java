package net.sparkminds.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Verify;

public interface VerifyRepository extends JpaRepository<Verify, Long>{
	Verify findByLink(String link);
	
	Verify findByOtp(String otp);
	
	List<Verify> findByAccountId(Long accountId);
}
