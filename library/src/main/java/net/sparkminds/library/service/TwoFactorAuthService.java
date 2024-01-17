package net.sparkminds.library.service;

import net.sparkminds.library.dto.mfa.MfaRequest;
import net.sparkminds.library.dto.mfa.MfaResponse;

public interface TwoFactorAuthService {
	MfaResponse generateTwoFactorAuth(MfaRequest mfaRequest);
	
	void verifyTwoFactorAuth(String email, String secret);
}
