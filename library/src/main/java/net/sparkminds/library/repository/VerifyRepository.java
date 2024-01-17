package net.sparkminds.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Verify;

public interface VerifyRepository extends JpaRepository<Verify, Long>{
	Optional<Verify> findByLink(String link);
	
	Optional<Verify> findByOtp(String otp);
	
	List<Verify> findByAccountId(Long accountId);
}
