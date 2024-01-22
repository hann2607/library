package net.sparkminds.library.service;

import net.sparkminds.library.dto.changeinfo.ChangePhoneRequest;

public interface ChangePhoneService {
	void changePhone(ChangePhoneRequest changePhoneRequest);

	void verifyChangePhone(String otp);
}
