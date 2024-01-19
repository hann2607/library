package net.sparkminds.library.service;

import net.sparkminds.library.dto.changeinfo.ChangeEmailRequest;
import net.sparkminds.library.dto.changeinfo.ChangePassRequest;
import net.sparkminds.library.dto.changeinfo.ChangePhoneRequest;
import net.sparkminds.library.dto.changeinfo.ResetPassRequest;

public interface ChangeInfoService {
	void resetPassword(ResetPassRequest resetPassRequest);

	void changePassword(ChangePassRequest changePassRequest);

	void changePhone(ChangePhoneRequest changePhoneRequest);

	void verifyChangePhone(String otp);

	void changeEmail(ChangeEmailRequest changeEmailRequest);

	void verifyChangeEmail(String otp);
}
