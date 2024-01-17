package net.sparkminds.library.event.authentication;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.jwt.JwtRequest;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.service.CustomerService;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuthenticationListener {
	
	private final CustomerService customerService;
	private final MessageSource messageSource;
	
	@EventListener
	private void handleAuthenticationVerify(VerifyAccountEvent verifyAccountEvent) {
		String message = null;
		Customer customer = null;
		JwtRequest jwtRequest = null;
		
		jwtRequest = verifyAccountEvent.getJwtRequest();
		
		customer = customerService.findByEmail(jwtRequest.getUsername());
		
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
	private void handleAuthenticationFailure(AuthenticationEvent authenticationEvent) {
		Customer customer = null;
		JwtRequest jwtRequest = null;
		
		jwtRequest = authenticationEvent.getJwtRequest();	
		
		customer = customerService.findByEmail(jwtRequest.getUsername());
		
		if (customer.getLoginAttempt() < 2) {
			customer.setLoginAttempt(customer.getLoginAttempt() + 1);
			customerService.update(customer);
		} else {
			handleAccountBlocked(customer);
		}
	}

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
}
