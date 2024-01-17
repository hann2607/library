package net.sparkminds.library.event.authentication;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.jwt.JwtRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.service.AccountService;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuthenticationListener {
	
	private final AccountService accountService;
	private final MessageSource messageSource;
	private final GoogleAuthenticator GOOGLE_AUTH = new GoogleAuthenticator();
	
	@EventListener
	private void handleAuthenticationVerify(VerifyAccountEvent verifyAccountEvent) {
		String message = null;
		Account account = null;
		JwtRequest jwtRequest = null;
		
		jwtRequest = verifyAccountEvent.getJwtRequest();
		
		account = accountService.findByEmail(jwtRequest.getUsername()).get(0);
		
		// Check verify account
		if(!account.isVerify()) {
			message = messageSource.getMessage("account.account-notVerified", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
					"account.account-notVerified");
		}
			
		if(account.isMfa()) {
			// Check Code nullable
			if(jwtRequest.getCode() == null || jwtRequest.getCode() == "") {
				
				message = messageSource.getMessage("account.mfa.code-invalid", 
						null, LocaleContextHolder.getLocale());
				
				log.error(message);
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"account.mfa.code-invalid");
			}
			
			// Check MFA
			if(!GOOGLE_AUTH.authorize(account.getSecret(), Integer.parseInt(jwtRequest.getCode()))) {
				
				message = messageSource.getMessage("account.mfa-invalid", 
						null, LocaleContextHolder.getLocale());
				
				log.error(message);
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"account.mfa-invalid");
			}
		}
	}
	
	@EventListener
	private void handleAuthenticationFailure(AuthenticationEvent authenticationEvent) {
		Account account = null;
		JwtRequest jwtRequest = null;
		
		jwtRequest = authenticationEvent.getJwtRequest();	
		
		account = accountService.findByEmail(jwtRequest.getUsername()).get(0);
		
		if (account.getLoginAttempt() < 2) {
			account.setLoginAttempt(account.getLoginAttempt() + 1);
			accountService.update(account);
		} else {
			handleAccountBlocked(account);
		}
	}

	private void handleAccountBlocked(Account account) {
		String message = null;
		
		if (account.getStatus().compareTo(EnumStatus.BLOCKED) != 0) {
			LocalDateTime currentDateTime = LocalDateTime.now();
			account.setBlockedAt(currentDateTime.plusMinutes(30));
			account.setReasonBlocked("Login failed more than 3 times");
			account.setStatus(EnumStatus.BLOCKED);

			accountService.update(account);
		} else {
			LocalDateTime currentDateTime = LocalDateTime.now();
			long seconds = Duration.between(currentDateTime, account.getBlockedAt()).getSeconds();

			if (seconds > 0) {
				message = messageSource.getMessage("account.account-blocked", null,
						LocaleContextHolder.getLocale());
				log.error(message);
				throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), "account.account-blocked");
			}

			account.setBlockedAt(null);
			account.setReasonBlocked(null);
			account.setStatus(EnumStatus.ACTIVE);
			account.setLoginAttempt(1);

			accountService.update(account);
		}
	}
}
