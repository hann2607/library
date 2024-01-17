package net.sparkminds.library.restcontroller.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.register.RegisterRequest;
import net.sparkminds.library.service.RegisterService;
import net.sparkminds.library.service.VerifyAccountService;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/common")
public class RegisterRestController {

	private final RegisterService registerService;
	private final VerifyAccountService verifyAccountService;

	@PostMapping("/register")
	public ResponseEntity<RegisterRequest> registerAccount(@Valid @RequestBody RegisterRequest userDTO) {
		return ResponseEntity.ok(registerService.register(userDTO));
	}
	
	@GetMapping("/register/verify/link/{otp}")
	public ResponseEntity<Void> verifyAccountByLink(@PathVariable("otp") String otp) {
		verifyAccountService.verifyAccountByLink(otp);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/register/verify/otp/{otp}")
	public ResponseEntity<Void> verifyAccountByOtp(@PathVariable("otp") String otp) {
		verifyAccountService.verifyAccountByOTP(otp);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/register/verify/resend/{email}")
	public ResponseEntity<Void> resendOtpAndLink(@PathVariable("email") String email) {
		verifyAccountService.resendOtpAndLink(email);
		return ResponseEntity.ok().build();
	}
}
