package net.sparkminds.library.restcontroller.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.jwt.JwtRequest;
import net.sparkminds.library.dto.jwt.JwtResponse;
import net.sparkminds.library.dto.jwt.RefreshTokenRequest;
import net.sparkminds.library.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/common")
public class AuthenticationRestController {

	
	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<JwtResponse> createAuthenticationToken(@Valid @RequestBody JwtRequest jwtRequest,
			HttpServletResponse response) {
		return ResponseEntity.ok(authenticationService.authentication(jwtRequest));
	}
	
	@PostMapping("/refreshtoken")
	public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
			HttpServletResponse response) {
		return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
	}
}
