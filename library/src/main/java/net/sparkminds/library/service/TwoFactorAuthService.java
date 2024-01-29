package net.sparkminds.library.service;

import net.sparkminds.library.dto.mfa.MfaResponse;

public interface TwoFactorAuthService {
	MfaResponse generateTwoFactorAuth();
	
	void verifyTwoFactorAuth(String email, String secret);
}
