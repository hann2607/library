package net.sparkminds.library.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {

	private final JwtUtil jwtUtil;
	private final AccountService accountService;
	private final UserDetailsServiceImpl userDetailsService;
	private final EncryptionService encryptionService;
	private final AuthenticationManager authenticationManager;
	private final MessageSource messageSource;
	

	@Override
	public JwtResponse authentication(JwtRequest jwtRequest) {
		UserDetails userDetails = null;
		String jwt = null;
		List<String> roles = new ArrayList<>();
		List<Account> account = new ArrayList<>();
		String message = null;

		// Find Account by Email
		account = accountService.findByEmail(jwtRequest.getUsername());
		if (account.isEmpty()) {
			message = messageSource.getMessage("Email.Account.email", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), 
					"Email.Account.email");
		}

		// Check password
		if (!encryptionService.compare(jwtRequest.getPassword(), 
				account.get(0).getPassword())) {
			message = messageSource.getMessage("account.password.password-invalid", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.password.password-invalid");
		}

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),
					jwtRequest.getPassword()));
		} catch (DisabledException e) {
			message = messageSource.getMessage("account.active.active-notactived", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
					"account.active.active-notactived");
		} catch (UsernameNotFoundException e) {
			message = messageSource.getMessage("Find.Error.Account.email", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
					"Find.Error.Account.email");
		}

		userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
		roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);

		return new JwtResponse(jwt, userDetails.getUsername(), roles);
	}
}
