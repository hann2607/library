package net.sparkminds.library.service;

import java.util.List;

import net.sparkminds.library.entity.Verify;

public interface VerifyService {
	void create(Verify verify);
	
	void update(Verify verify);
	
	void delete(Long verifyId);
	
	Verify findByOtp(String otp);
	
	List<Verify> findByAccountId(Long accountId);
}
