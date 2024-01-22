package net.sparkminds.library.restcontroller.publics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.changeinfo.ChangePassRequest;
import net.sparkminds.library.dto.changeinfo.ConfirmResetPassRequest;
import net.sparkminds.library.dto.changeinfo.ResetPassRequest;
import net.sparkminds.library.service.PasswordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Password", description = "Password APIs")
public class UserRestController {

	private final PasswordService passwordService;
	
	@Operation(summary = "Reset password", 
			description = "Reset password.", 
			tags = { "Password", "post" })
	@PostMapping("/common/resetpass")
	public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPassRequest resetPassRequest) {
		passwordService.resetPassword(resetPassRequest);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Confirm reset password", 
			description = "Confirm reset password.", 
			tags = { "Password", "post" })
	@PostMapping("/common/confirmresetpass")
	public ResponseEntity<Void> confirmResetPass(@Valid @RequestBody ConfirmResetPassRequest confirmResetPassRequest) {
		passwordService.confirmResetPassword(confirmResetPassRequest);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Change password", 
			description = "Change password.", 
			tags = { "Password", "post" })
	@PostMapping("/user/changepass")
	public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePassRequest changePassRequest) {
		passwordService.changePassword(changePassRequest);
		return ResponseEntity.ok().build();
	}
}
