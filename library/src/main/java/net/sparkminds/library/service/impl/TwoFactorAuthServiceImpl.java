package net.sparkminds.library.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.mfa.MfaRequest;
import net.sparkminds.library.dto.mfa.MfaResponse;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.service.CustomerService;
import net.sparkminds.library.service.TwoFactorAuthService;

@Service
@RequiredArgsConstructor
@Log4j2
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {

	private final CustomerService customerService;
	private final MessageSource messageSource;
	private final GoogleAuthenticator GOOGLE_AUTH = new GoogleAuthenticator();

	@Override
	public MfaResponse generateTwoFactorAuth(MfaRequest mfaRequest) {
		MfaResponse mfaResponse = null;
		String secret = null;
		String qrCodeUrl = null;
		GoogleAuthenticatorKey credentials = null;

		credentials = GOOGLE_AUTH.createCredentials();
		secret = credentials.getKey();
		qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("LibMana", mfaRequest.getEmail(), credentials);

		mfaResponse = MfaResponse.builder().secret(secret).qrcode(qrCodeUrl).build();

		return mfaResponse;
	}

	@Override
	public void verifyTwoFactorAuth(String email, String secret) {
		Customer customer = null;
		String message = null;
		
		customer = customerService.findByEmail(email);
		if(customer == null) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"account.email.email-notfound");
		}
		
		customer.setMfa(true);
		customer.setSecret(secret);
		
		customerService.update(customer);
	}
}
