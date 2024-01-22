package net.sparkminds.library.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.jwt.JwtRequest;
import net.sparkminds.library.dto.jwt.JwtResponse;
import net.sparkminds.library.dto.jwt.RefreshTokenRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.Session;
import net.sparkminds.library.event.authentication.AuthenticationFailureEvent;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.jwt.JwtUtil;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.repository.SessionRepository;
import net.sparkminds.library.service.AuthenticationService;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {

	private final JwtUtil jwtUtil;
	private final AccountRepository accountRepository;
	private final SessionRepository sessionRepository;
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthenticationManager authenticationManager;
	private final MessageSource messageSource;
	private final ApplicationEventPublisher eventPublisher;
	private final GoogleAuthenticator GOOGLE_AUTH = new GoogleAuthenticator();

	@Override
	public JwtResponse authentication(JwtRequest jwtRequest) {
		Optional<Account> account = null;
		String message = null;
		
		// Login success
		if (authenticateUser(jwtRequest)) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
			List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());
			
			// Reset login attempt
			account = accountRepository.findByEmail(jwtRequest.getUsername());
			if(!account.isPresent()) {
				message = messageSource.getMessage("account.email.email-notfound", 
						null, LocaleContextHolder.getLocale());
				
				log.error(message + ": " + jwtRequest.getUsername());
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"account.email.email-notfound");
			}
			
			account.get().setLoginAttempt(0);
			account.get().setFirstTimeLogin(false);
			try {
				accountRepository.save(account.get());
				message = messageSource.getMessage("account.update-successed", 
						null, LocaleContextHolder.getLocale());
				
				log.info(message + ": " + account.get().toString());
			} catch (Exception e) {
				message = messageSource.getMessage("account.update-failed", 
						null, LocaleContextHolder.getLocale());
				
				log.error(message + ": " + account.get().toString());
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"account.update-failed");
			}

			// Create token and refresh token
			String JTI = UUID.randomUUID().toString();
			String token = jwtUtil.generateToken(userDetails.getUsername(), roles, JTI);
			String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername(), roles, JTI);
			Date refreshTokenExpiration = jwtUtil.extractExpiration(refreshToken);

			// Save to table session
			Session session = Session.builder().jti(JTI).isLogin(true)
					.refreshExpirationTime(convertDateToLocalDateTime(refreshTokenExpiration))
					.account(account.get()).build();

			try {
				sessionRepository.save(session);
				message = messageSource.getMessage("session.insert-successed", 
						null, LocaleContextHolder.getLocale());
				
				log.info(message + ": " + session.toString());
			} catch (Exception e) {
				message = messageSource.getMessage("session.insert-failed", 
						null, LocaleContextHolder.getLocale());
				
				log.error(message + ": " + session.toString());
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"session.insert-failed");
			}

			JwtResponse jwtResponse = new JwtResponse(token, userDetails.getUsername(), roles, refreshToken);
			
			return jwtResponse;
		}
		
		return null;
	}

	private boolean authenticateUser(JwtRequest jwtRequest) {
		String message = null;
		Optional<Account> account = null;
		
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
			
			// Find account
			account = accountRepository.findByEmail(jwtRequest.getUsername());
			if(!account.isPresent()) {
				message = messageSource.getMessage("account.email.email-notfound", 
						null, LocaleContextHolder.getLocale());
				
				log.error(message + ": " + jwtRequest.getUsername());
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"account.email.email-notfound");
			}
			
			// Check verify account
			if(!account.get().isVerify()) {
				message = messageSource.getMessage("account.account-notVerified", 
						null, LocaleContextHolder.getLocale());
				
				log.error(message);
				throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
						"account.account-notVerified");
			}
				
			// Check MFA enabled
			if(account.get().isMfa()) {
				// Check Code nullable
				if(jwtRequest.getCode() == null || jwtRequest.getCode() == "") {
					
					message = messageSource.getMessage("account.mfa.code-invalid", 
							null, LocaleContextHolder.getLocale());
					
					log.error(message);
					throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
							"account.mfa.code-invalid");
				}
				
				// Check MFA
				if(!GOOGLE_AUTH.authorize(account.get().getSecret(), Integer.parseInt(jwtRequest.getCode()))) {
					
					message = messageSource.getMessage("account.mfa-invalid", 
							null, LocaleContextHolder.getLocale());
					
					log.error(message);
					throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
							"account.mfa-invalid");
				}
			}
			
			return true;
		} catch (UsernameNotFoundException e) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"account.email.email-notfound");
		} catch (BadCredentialsException e) {
			
			// handle event Authentication Failure 
			eventPublisher.publishEvent(new AuthenticationFailureEvent(this, jwtRequest));
			
			message = messageSource.getMessage("account.password.password-invalid", 
			null, LocaleContextHolder.getLocale());
	
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.password.password-invalid");
		} 
	}
	
	private static LocalDateTime convertDateToLocalDateTime(Date date) {
		Instant instant = date.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		return LocalDateTime.ofInstant(instant, zoneId);
	}

	@Override
	public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
		Optional<Session> session = null;
		LocalDateTime currentDateTime = null;
		String message = null;
		String token = null;
		List<String> roles = new ArrayList<>();
		UserDetails userDetails = null;
		String refreshToken = null;
		String JTI = null;
		long seconds = 0;

		refreshToken = refreshTokenRequest.getRefreshToken();
		session = sessionRepository.findByJti(jwtUtil.extractJTI(refreshToken));

		// Check if it's token?
		if (!session.isPresent()) {
			message = messageSource.getMessage("refreshtoken.refreshtoken-notfound", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(), "refreshtoken.refreshtoken-notfound");
		}

		// Check if it's expired?
		currentDateTime = LocalDateTime.now();
		seconds = Duration.between(currentDateTime, session.get().getRefreshExpirationTime()).getSeconds();
		if (seconds < 0) {
			message = messageSource.getMessage("refreshtoken.refreshtoken-isexpired", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), "refreshtoken.refreshtoken-isexpired");
		}

		// Check if it's refreshToken?
		seconds = Duration.between(session.get().getRefreshExpirationTime(),
				convertDateToLocalDateTime(jwtUtil.extractExpiration(refreshToken))).getSeconds();
		if (seconds != 0) {
			message = messageSource.getMessage("refreshtoken.refreshtoken-invalid", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), "refreshtoken.refreshtoken-invalid");
		}

		// Generate new token
		JTI = session.get().getJti();
		userDetails = userDetailsService.loadUserByUsername(jwtUtil.extractUsername(refreshToken));
		roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

		token = jwtUtil.generateToken(userDetails.getUsername(), roles, JTI);

		return new JwtResponse(token, userDetails.getUsername(), roles, refreshToken);
	}
}
