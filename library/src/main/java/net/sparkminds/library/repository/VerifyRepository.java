package net.sparkminds.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.enumration.EnumTypeOTP;

public interface VerifyRepository extends JpaRepository<Verify, Long>{
	Optional<Verify> findByLink(String link);
	
	Optional<Verify> findByOtpAndTypeOTP(String otp, EnumTypeOTP type);
	
	List<Verify> findByAccountId(Long accountId);
}
