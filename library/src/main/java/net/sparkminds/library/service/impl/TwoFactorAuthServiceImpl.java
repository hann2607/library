package net.sparkminds.library.service.impl;

import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.mfa.MfaResponse;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.service.LogoutService;
import net.sparkminds.library.service.TwoFactorAuthService;

@Service
@RequiredArgsConstructor
@Log4j2
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {

	private final AccountRepository accountRepository;
	private final MessageSource messageSource;
	private final LogoutService logoutService;
	private final GoogleAuthenticator GOOGLE_AUTH = new GoogleAuthenticator();

	@Override
	public MfaResponse generateTwoFactorAuth() {
		MfaResponse mfaResponse = null;
		String secret = null;
		String qrCodeUrl = null;
		GoogleAuthenticatorKey credentials = null;
		UserDetails principal = null;
		String message = null;

		principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal == null) {
    		message = messageSource.getMessage("account.account-logout", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), 
					"account.account-logout");
    	}
		credentials = GOOGLE_AUTH.createCredentials();
		secret = credentials.getKey();
		qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("LibMana", principal.getUsername(), credentials);

		mfaResponse = MfaResponse.builder().secret(secret).qrcode(qrCodeUrl).build();

		return mfaResponse;
	}

	@Override
	public void verifyTwoFactorAuth(String code, String secret) {
		Optional<Account> account = null;
		String message = null;
		UserDetails principal = null;
		
		principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal == null) {
    		message = messageSource.getMessage("account.account-logout", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), 
					"account.account-logout");
    	}
		
		if(!GOOGLE_AUTH.authorize(secret, Integer.parseInt(code))) {
			message = messageSource.getMessage("account.mfa-invalid", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.mfa-invalid");
		}
		
		account = accountRepository.findByEmail(principal.getUsername());
		if(!account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"account.email.email-notfound");
		}
		
		account.get().setMfa(true);
		account.get().setSecret(secret);
		
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
		
		logoutService.logout();
	}
}
