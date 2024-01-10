package net.sparkminds.library.service;

import org.springframework.http.ResponseEntity;

import net.sparkminds.library.dto.register.RegisterRequest;

public interface RegisterService {
	ResponseEntity<RegisterRequest> register(RegisterRequest userDTO);
	
	void verifyAccountByLink(String otp);
	
	void verifyAccountByOTP(String otp);
	
	void resendOtpAndLink(String email);
}
