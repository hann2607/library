package net.sparkminds.library.restcontroller.publics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.mfa.MfaResponse;
import net.sparkminds.library.service.TwoFactorAuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "MFA", description = "MFA APIs")
public class TwoFactorAuthRestController {
	
	private final TwoFactorAuthService twoFactorAuthService;
	
	@Operation(summary = "Genarate MFA", 
			description = "The response is MfaResponse object with secret and qrCode.", 
			tags = { "MFA", "post" })
	@PostMapping("/generatetwofa")
    public ResponseEntity<MfaResponse> generateTwoFactorAuth() {
        return ResponseEntity.ok(twoFactorAuthService.generateTwoFactorAuth());
    }
	
	@Operation(summary = "Enable MFA", 
			description = "Enable MFA.", 
			tags = { "MFA", "post" })
	@PostMapping("/enabletwofa")
    public ResponseEntity<Void> enableTwoFactorAuth(
    		@Parameter(description = "Secret key genarate MFA") @RequestParam String secret, 
    		@Parameter(description = "Email enable MFA") @RequestParam String code) {
		twoFactorAuthService.verifyTwoFactorAuth(code, secret);
        return ResponseEntity.ok().build();
    }
}
