package net.sparkminds.library.service;

import net.sparkminds.library.entity.Verify;

public interface VerifyService {
	void save(Verify verify);
	
	void delete(Long verifyId);
	
	Verify findByLink(String link);
	
	Verify findByOtp(String otp);
}
