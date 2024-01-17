package net.sparkminds.library.service.impl;

import org.springframework.stereotype.Service;

import net.sparkminds.library.dto.mfa.MfaResponse;
import net.sparkminds.library.service.TwoFactorAuthService;

@Service
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService{

	@Override
	public MfaResponse generateTwoFactorAuth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyTwoFactorAuth(String email, String code) {
		// TODO Auto-generated method stub
		return false;
	}

}
