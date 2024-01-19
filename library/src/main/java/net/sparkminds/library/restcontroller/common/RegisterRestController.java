package net.sparkminds.library.restcontroller.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.register.RegisterRequest;
import net.sparkminds.library.service.RegisterService;
import net.sparkminds.library.service.VerifyAccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/common")
@Tag(name = "Register", description = "Register APIs")
public class RegisterRestController {

	private final RegisterService registerService;
	private final VerifyAccountService verifyAccountService;

	@Operation(summary = "Register new account", 
				description = "Register new account. The response is account object with email, "
						+ "password, firstname, lastname, phone, address, status and avatar.", 
				tags = { "Register", "post" })
	@PostMapping("/register")
	public ResponseEntity<RegisterRequest> registerAccount(@Valid @RequestBody RegisterRequest userDTO) {
		return ResponseEntity.ok(registerService.register(userDTO));
	}

	@Operation(summary = "Verify account by Link", 
			description = "Verify account by Link.", 
			tags = { "Register", "get" })
	@GetMapping("/register/verify/link/{otp}")
	public ResponseEntity<Void> verifyAccountByLink(@PathVariable("otp") String otp) {
		verifyAccountService.verifyAccountByLink(otp);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Verify account by OTP", 
			description = "Verify account by OTP.", 
			tags = { "Register", "post" })
	@PostMapping("/register/verify/otp/{otp}")
	public ResponseEntity<Void> verifyAccountByOtp(@Parameter(description = "Verify account by OTP")
		@PathVariable("otp") String otp) {
		verifyAccountService.verifyAccountByOTP(otp);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Resend Link and OTP verify email", 
			description = "Resend Link and OTP verify email.", 
			tags = { "Register", "post" })
	@PostMapping("/register/verify/resend/{email}")
	public ResponseEntity<Void> resendOtpAndLink(@Parameter(description = "Email resend Link and OTP") 
		@PathVariable("email") String email) {
		verifyAccountService.resendOtpAndLink(email);
		return ResponseEntity.ok().build();
	}
}
