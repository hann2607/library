package net.sparkminds.library.service;

import net.sparkminds.library.dto.changeinfo.ChangePassRequest;
import net.sparkminds.library.dto.changeinfo.ConfirmResetPassRequest;
import net.sparkminds.library.dto.changeinfo.ResetPassRequest;

public interface PasswordService {
	void resetPassword(ResetPassRequest resetPassRequest);
	
	void confirmResetPassword(ConfirmResetPassRequest confirmResetPassRequest);

	void changePassword(ChangePassRequest changePassRequest);
}
