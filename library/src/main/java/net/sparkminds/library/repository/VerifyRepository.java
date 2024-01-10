package net.sparkminds.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Verify;

public interface VerifyRepository extends JpaRepository<Verify, Long>{
	Verify findByLink(String link);
	Verify findByOtp(String otp);
}
