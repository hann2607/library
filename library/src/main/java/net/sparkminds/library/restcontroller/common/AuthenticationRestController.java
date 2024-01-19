package net.sparkminds.library.restcontroller.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.changeinfo.ChangePassRequest;
import net.sparkminds.library.dto.changeinfo.ResetPassRequest;
import net.sparkminds.library.dto.jwt.JwtRequest;
import net.sparkminds.library.dto.jwt.JwtResponse;
import net.sparkminds.library.dto.jwt.RefreshTokenRequest;
import net.sparkminds.library.service.AuthenticationService;
import net.sparkminds.library.service.ChangeInfoService;
import net.sparkminds.library.service.LogoutService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/common")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthenticationRestController {
	
	private final AuthenticationService authenticationService;
	private final LogoutService logoutService;
	private final ChangeInfoService changeInfoService;

	@Operation(summary = "Authentication", 
			description = "The response is JwtResponse object with token, "
					+ "refreshToken, type, username, roles.", 
			tags = { "Authentication", "post" })
	@PostMapping("/login")
	public ResponseEntity<JwtResponse> createAuthenticationToken(@Valid @RequestBody JwtRequest jwtRequest,
			HttpServletResponse response) {
		return ResponseEntity.ok(authenticationService.authentication(jwtRequest));
	}
	
	@Operation(summary = "Refresh token", 
			description = "The response is JwtResponse object with token, "
					+ "refreshToken, type, username, roles.", 
			tags = { "Authentication", "post" })
	@PostMapping("/refreshtoken")
	public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
			HttpServletResponse response) {
		return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
	}
	
	@Operation(summary = "Logout", 
			description = "Logout and block session.", 
			tags = { "Authentication", "post" })
	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		logoutService.logout();
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Reset password", 
			description = "Reset password.", 
			tags = { "Authentication", "post" })
	@PostMapping("/resetpass")
	public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPassRequest resetPassRequest) {
		changeInfoService.resetPassword(resetPassRequest);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Change password", 
			description = "Change password.", 
			tags = { "Authentication", "post" })
	@PostMapping("/changepass")
	public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePassRequest changePassRequest) {
		changeInfoService.changePassword(changePassRequest);
		return ResponseEntity.ok().build();
	}
}
