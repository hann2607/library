package net.sparkminds.library.restcontroller.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.jwt.ErrorResponse;
import net.sparkminds.library.jwt.JwtRequest;
import net.sparkminds.library.jwt.JwtResponse;
import net.sparkminds.library.jwt.JwtUtil;
import net.sparkminds.library.service.AccountService;
import net.sparkminds.library.service.impl.UserDetailsServiceImpl;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class AuthenticationRestController {

	private final JwtUtil jwtUtil;

	private final AccountService accountService;

	private final AuthenticationManager authenticationManager;

	private final UserDetailsServiceImpl userDetailsService;

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest jwtRequest, HttpServletResponse response)
			throws BadCredentialsException, DisabledException, UsernameNotFoundException, IOException {

		UserDetails userDetails = null;
		String jwt = null;
		List<String> roles = new ArrayList<>();
		List<Account> account = new ArrayList<>();

		account = accountService.findByEmail(jwtRequest.getUsername());
		if (account.isEmpty()) {
			ErrorResponse errorResponse = ErrorResponse.from(HttpStatus.UNAUTHORIZED, "username/password",
					"Username/Password incorrect", "");
			return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
		}

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
		} catch (BadCredentialsException e) {
			ErrorResponse errorResponse = ErrorResponse.from(HttpStatus.UNAUTHORIZED, "username/password",
					"Username/Password incorrect", "");
			return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
		} catch (DisabledException e) {
			ErrorResponse errorResponse = ErrorResponse.from(HttpStatus.NOT_FOUND, "account", "User is not activated",
					"");
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
		} catch (UsernameNotFoundException e) {
			ErrorResponse errorResponse = ErrorResponse.from(HttpStatus.NOT_FOUND, "account", "User not found", "");
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
		}

		userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());

		roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

		jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
	}
}
