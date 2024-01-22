package net.sparkminds.library.service;

import net.sparkminds.library.dto.changeinfo.ChangeEmailRequest;

public interface ChangeEmailService {
	void changeEmail(ChangeEmailRequest changeEmailRequest);

	void verifyChangeEmail(String otp);
}
