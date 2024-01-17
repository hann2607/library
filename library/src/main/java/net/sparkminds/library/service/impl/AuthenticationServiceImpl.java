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
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.mail.PasswordAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.jwt.JwtRequest;
import net.sparkminds.library.dto.jwt.JwtResponse;
import net.sparkminds.library.dto.jwt.RefreshTokenRequest;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.entity.Session;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.event.AuthenticationEvent;
import net.sparkminds.library.event.AuthenticationSuccessEvent;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.jwt.JwtUtil;
import net.sparkminds.library.service.AuthenticationService;
import net.sparkminds.library.service.CustomerService;
import net.sparkminds.library.service.EncryptionService;
import net.sparkminds.library.service.SessionService;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {

	private final JwtUtil jwtUtil;
	private final CustomerService customerService;
	private final SessionService sessionService;
	private final UserDetailsServiceImpl userDetailsService;
	private final EncryptionService encryptionService;
	private final AuthenticationManager authenticationManager;
	private final MessageSource messageSource;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public JwtResponse authentication(JwtRequest jwtRequest) {
		Customer customer = null;
		
		// Xử lý logic đăng nhập
		if (authenticateUser(jwtRequest)) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
			List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());

			// Reset login attempt
			customer = customerService.findByEmail(jwtRequest.getUsername());
			customer.setLoginAttempt(0);
			customerService.update(customer);

			// Create token and refresh token
			String JTI = UUID.randomUUID().toString();
			String token = jwtUtil.generateToken(userDetails.getUsername(), roles, JTI);
			String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername(), roles, JTI);
			Date refreshTokenExpiration = jwtUtil.extractExpiration(refreshToken);

			// Save to table session
			Session session = Session.builder().jti(JTI).isLogin(true)
					.refreshExpirationTime(convertDateToLocalDateTime(refreshTokenExpiration)).account(customer)
					.build();

			sessionService.create(session);

			JwtResponse jwtResponse = new JwtResponse(token, userDetails.getUsername(), roles, refreshToken);
			AuthenticationSuccessEvent authenticationSuccessEvent = new AuthenticationSuccessEvent(this, jwtResponse);
			eventPublisher.publishEvent(authenticationSuccessEvent);

			return jwtResponse;
		} else {
			// Xử lý logic khi đăng nhập thất bại
			handleAuthenticationFailure(jwtRequest.getUsername());
			return null; // Hoặc throw exception tùy thuộc vào yêu cầu của bạn
		}
	}

	private boolean authenticateUser(JwtRequest jwtRequest) {
		
		
		try {
			handleAuthenticationVerify(jwtRequest.getUsername());
			
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
			return true;
		} catch (AuthenticationException e) {
			handleAuthenticationFailure(jwtRequest.getUsername());
			return false;
		} 
	}

	@EventListener
	private void handleAuthenticationVerify(String username) {
		String message = null;
		Customer customer = null;
		
		customer = customerService.findByEmail(username);
		
		// Check verify account
		if(!customer.isVerify()) {
			message = messageSource.getMessage("account.account-notVerified", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
					"account.account-notVerified");
		}
	}
	
	@EventListener
	private void handleAuthenticationFailure(String username) {
		Customer customer = null;
		
		customer = customerService.findByEmail(username);
		
		if (customer.getLoginAttempt() < 2) {
			customer.setLoginAttempt(customer.getLoginAttempt() + 1);
			customerService.update(customer);
		} else {
			handleAccountBlocked(customer);
		}
	}

	@EventListener
	private void handleAccountBlocked(Customer customer) {
		String message = null;
		
		if (customer.getStatus().compareTo(EnumStatus.BLOCKED) != 0) {
			LocalDateTime currentDateTime = LocalDateTime.now();
			customer.setBlockedAt(currentDateTime.plusMinutes(30));
			customer.setReasonBlocked("Login failed more than 3 times");
			customer.setStatus(EnumStatus.BLOCKED);

			customerService.update(customer);
		} else {
			LocalDateTime currentDateTime = LocalDateTime.now();
			long seconds = Duration.between(currentDateTime, customer.getBlockedAt()).getSeconds();

			if (seconds > 0) {
				message = messageSource.getMessage("account.account-blocked", null,
						LocaleContextHolder.getLocale());
				log.error(message);
				throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), "account.account-blocked");
			}

			customer.setBlockedAt(null);
			customer.setReasonBlocked(null);
			customer.setStatus(EnumStatus.ACTIVE);
			customer.setLoginAttempt(1);

			customerService.update(customer);
		}
	}

//	@Override
//	public JwtResponse authentication(JwtRequest jwtRequest) {
//		UserDetails userDetails = null;
//		String token = null;
//		String refreshToken = null;
//		List<String> roles = new ArrayList<>();
//		Customer customer = null;
//		String message = null;
//		LocalDateTime currentDateTime = null;
//		String JTI = null;
//
//		// Find Account by Email
//		customer = customerService.findByEmail(jwtRequest.getUsername());
//		if (customer == null) {
//			message = messageSource.getMessage("account.email.email-notfound", 
//					null, LocaleContextHolder.getLocale());
//			
//			log.error(message);
//			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), 
//					"account.email.email-notfound");
//		}
//
//		// Check verify account
//		if(!customer.isVerify()) {
//			message = messageSource.getMessage("account.account-notVerified", 
//					null, LocaleContextHolder.getLocale());
//			
//			log.error(message);
//			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
//					"account.account-notVerified");
//		}
//		
//		// Check password
//		if (!encryptionService.compare(jwtRequest.getPassword(), 
//				customer.getPassword())) { 
//			
//			// Check login attempt
//			if(customer.getLoginAttempt() < 2) {
//				customer.setLoginAttempt(customer.getLoginAttempt() + 1);
//				customerService.update(customer);
//			} else {
//				
//				// Check status blocked
//				if(customer.getStatus().compareTo(EnumStatus.BLOCKED) != 0) {
//					currentDateTime= LocalDateTime.now();
//					customer.setBlockedAt(currentDateTime.plusMinutes(30));
//	        		customer.setReasonBlocked("Login failed more than 3 times");
//	        		customer.setStatus(EnumStatus.BLOCKED);
//	        		
//	        		customerService.update(customer);
//				} else {
//					
//					// if is active -> blocked
//					currentDateTime= LocalDateTime.now();
//		        	long seconds = Duration.between(currentDateTime, customer.getBlockedAt()).getSeconds();
//
//		        	// Check time block
//					if(seconds > 0) {
//						message = messageSource.getMessage("account.account-blocked", null,
//		    					LocaleContextHolder.getLocale());
//		    			log.error(message);
//		    			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
//		    					"account.account-blocked");
//					} 
//					
//					customer.setBlockedAt(null);
//	        		customer.setReasonBlocked(null);
//	        		customer.setStatus(EnumStatus.ACTIVE);
//	        		customer.setLoginAttempt(1);
//	        		
//	        		customerService.update(customer);
//				}
//			}
//			
//			message = messageSource.getMessage("account.password.password-invalid", 
//					null, LocaleContextHolder.getLocale());
//			
//			log.error(message);
//			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
//					"account.password.password-invalid");
//		}
//
//		authenticationManager.authenticate(
//				new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),
//				jwtRequest.getPassword()));
//
//		userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
//		roles = userDetails.getAuthorities().stream()
//				.map(GrantedAuthority::getAuthority)
//				.collect(Collectors.toList());
//		
//		// Reset login attempt
//		customer.setLoginAttempt(0);
//		customerService.update(customer);
//		
//		// Create token and refresh token
//		JTI = UUID.randomUUID().toString();
//		token = jwtUtil.generateToken(userDetails.getUsername(), roles, JTI);
//		refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername(), roles, JTI);
//		Date refreshTokenExpiration = jwtUtil.extractExpiration(refreshToken);
//        
//		// Save to table session
//		Session session = Session.builder()
//				.jti(JTI)
//				.isLogin(true)
//				.refreshExpirationTime(convertDateToLocalDateTime(refreshTokenExpiration))
//				.account(customer).build();
//		
//		sessionService.create(session);
//
//		return new JwtResponse(token, userDetails.getUsername(), roles, refreshToken);
//	}

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
		session = sessionService.findByJti(jwtUtil.extractJTI(refreshToken));

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
