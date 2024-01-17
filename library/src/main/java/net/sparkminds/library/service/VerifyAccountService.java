package net.sparkminds.library.service;

public interface VerifyAccountService {
	void verifyAccountByLink(String otp);
	
	void verifyAccountByOTP(String otp);
	
	void resendOtpAndLink(String email);
}
