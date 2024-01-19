package net.sparkminds.library.restcontroller.publics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.changeinfo.ChangeEmailRequest;
import net.sparkminds.library.dto.changeinfo.ChangePhoneRequest;
import net.sparkminds.library.service.ChangeInfoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "Change infomation account", description = "ChangeInfo APIs")
public class ChangeInfoRestController {
	
	private final ChangeInfoService changeInfoService;
	
	@Operation(summary = "Change phone", 
			description = "Change phone.", 
			tags = { "Change infomation account", "post" })
	@PostMapping("/changephone")
	public ResponseEntity<Void> changePhone(@Valid @RequestBody ChangePhoneRequest changePhoneRequest) {
		changeInfoService.changePhone(changePhoneRequest);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Verify change phone", 
			description = "Verify change phone.", 
			tags = { "Change infomation account", "post" })
	@PostMapping("/verifychangephone")
	public ResponseEntity<Void> verifyChangePhone(@Parameter(description = "OTP") @RequestParam("otp") String otp) {
		changeInfoService.verifyChangePhone(otp);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Change email", 
			description = "Change email.", 
			tags = { "Change infomation account", "post" })
	@PostMapping("/changeemail")
	public ResponseEntity<Void> changeEmail(@Valid @RequestBody ChangeEmailRequest changeEmailRequest) {
		changeInfoService.changeEmail(changeEmailRequest);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Verify change email", 
			description = "Verify change email.", 
			tags = { "Change infomation account", "post" })
	@PostMapping("/verifychangeemail")
	public ResponseEntity<Void> verifyChangeEmail(@Parameter(description = "OTP") @RequestParam("otp") String otp) {
		changeInfoService.verifyChangeEmail(otp);
		return ResponseEntity.ok().build();
	}
}
