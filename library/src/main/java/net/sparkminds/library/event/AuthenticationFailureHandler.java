package net.sparkminds.library.event;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.jwt.JwtRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.AccountRepository;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuthenticationFailureHandler {
	
	private final AccountRepository accountRepository;
	private final MessageSource messageSource;
	
	@EventListener
	private void handleAuthenticationFailure(AuthenticationFailureEvent authenticationEvent) {
		Optional<Account> account = null;
		String message = null;
		JwtRequest jwtRequest = null;
		
		jwtRequest = authenticationEvent.getJwtRequest();	
		
		account = accountRepository.findByEmailAndStatus(jwtRequest.getUsername(), EnumStatus.ACTIVE);
		if(!account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + jwtRequest.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
		
		if (account.get().getLoginAttempt() < 2) {
			account.get().setLoginAttempt(account.get().getLoginAttempt() + 1);
			accountRepository.save(account.get());
		} else {
			handleAccountBlocked(account.get());
		}
	}

	private void handleAccountBlocked(Account account) {
		String message = null;
		
		if (account.getStatus().compareTo(EnumStatus.BLOCKED) != 0) {
			LocalDateTime currentDateTime = LocalDateTime.now();
			account.setBlockedAt(currentDateTime.plusMinutes(30));
			account.setReasonBlocked("Login failed more than 3 times");
			account.setStatus(EnumStatus.BLOCKED);

			accountRepository.save(account);
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

			accountRepository.save(account);
		}
	}
}
