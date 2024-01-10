package net.sparkminds.library.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.jwt.JwtRequest;
import net.sparkminds.library.jwt.JwtResponse;
import net.sparkminds.library.jwt.JwtUtil;
import net.sparkminds.library.service.AccountService;
import net.sparkminds.library.service.AuthenticationService;
import net.sparkminds.library.service.EncryptionService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final JwtUtil jwtUtil;

	private final AccountService accountService;

	private final UserDetailsServiceImpl userDetailsService;

	private final EncryptionService encryptionService;

	private final AuthenticationManager authenticationManager;

	@Override
	public JwtResponse authentication(JwtRequest jwtRequest) {
		UserDetails userDetails = null;
		String jwt = null;
		List<String> roles = new ArrayList<>();
		List<Account> account = new ArrayList<>();

		// Find Account by Email
		account = accountService.findByEmail(jwtRequest.getUsername());
		if (account.isEmpty()) {
			throw new RequestException("Email is invalid!");
		}

		// Check password
		if (!encryptionService.compare(jwtRequest.getPassword(), 
				account.get(0).getPassword())) {
			throw new RequestException("Password is invalid!");
		}

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),
					jwtRequest.getPassword()));
		} catch (DisabledException e) {
			throw new RequestException("User is not activated!");
		} catch (UsernameNotFoundException e) {
			throw new RequestException("User not found");
		}

		userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
		roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);

		return new JwtResponse(jwt, userDetails.getUsername(), roles);
	}
}