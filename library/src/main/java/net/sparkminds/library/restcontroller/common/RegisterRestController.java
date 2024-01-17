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

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/common")
public class RegisterRestController {

	private final RegisterService registerService;

	@PostMapping("/register")
	public ResponseEntity<RegisterRequest> registerAccount(@Valid @RequestBody RegisterRequest userDTO) {
		registerService.register(userDTO);
		return ResponseEntity.ok(userDTO);
	}
	
	@GetMapping("/register/verify/link/{otp}")
	public void verifyAccountByLink(@PathVariable("otp") String otp) {
		registerService.verifyAccountByLink(otp);
	}
	
	@PostMapping("/register/verify/otp/{otp}")
	public void verifyAccountByOtp(@PathVariable("otp") String otp) {
		registerService.verifyAccountByOTP(otp);
	}
	
	@PostMapping("/register/verify/resend/{email}")
	public void resendOtpAndLink(@PathVariable("email") String email) {
		registerService.resendOtpAndLink(email);
	}
}
