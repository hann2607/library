package net.sparkminds.library.restcontroller.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.mfa.MfaRequest;
import net.sparkminds.library.dto.mfa.MfaResponse;
import net.sparkminds.library.service.TwoFactorAuthService;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/common")
public class TwoFactorAuthController {
	
	private final TwoFactorAuthService twoFactorAuthService;
	
	@PostMapping("/generatetwofa")
    public ResponseEntity<MfaResponse> generateTwoFactorAuth(@Valid @RequestBody MfaRequest mfaRequest) {
        return ResponseEntity.ok(twoFactorAuthService.generateTwoFactorAuth(mfaRequest));
    }
	
	@PostMapping("/enabletwofa")
    public ResponseEntity<Void> enableTwoFactorAuth(@RequestParam String email, @RequestParam String secret) {
		twoFactorAuthService.verifyTwoFactorAuth(email, secret);
        return ResponseEntity.ok().build();
    }
}
