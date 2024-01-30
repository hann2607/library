package net.sparkminds.library.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.changeinfo.ChangePhoneRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.enumration.EnumTypeOTP;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.repository.CustomerRepository;
import net.sparkminds.library.repository.VerifyRepository;
import net.sparkminds.library.service.ChangePhoneService;
import net.sparkminds.library.service.LogoutService;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChangePhoneServiceImpl implements ChangePhoneService {

	private final AccountRepository accountRepository;        // Handle entities account
	private final CustomerRepository customerRepository;        // Handle entities customer
	private final VerifyRepository verifyRepository;        // Handle entities verify
	private final MessageSource messageSource;
	private final LogoutService logoutService;

	@Override
	public void changePhone(ChangePhoneRequest changePhoneRequest) {
		String newPhone = changePhoneRequest.getNewPhone();
		String message = null;
		Optional<Customer> customer = null;
		Optional<Account> account = null;
		LocalDateTime currentDateTime = null;
		UserDetails principal = null;
		
		principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal == null) {
    		message = messageSource.getMessage("account.account-logout", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), 
					"account.account-logout");
    	}
		
		// Found account by email
		customer = customerRepository.findByEmailAndStatus(principal.getUsername(), EnumStatus.ACTIVE);
		account = accountRepository.findByEmailAndStatus(principal.getUsername(), EnumStatus.ACTIVE);
		if (!customer.isPresent() || !account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + principal.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
		
		// Compare new phone with phone in DB
		if (customer.get().getPhone().equals(newPhone)) {
			message = messageSource.getMessage("account.newphone.newphone-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + principal.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.newphone.newphone-invalid");
		}
		
		// Create verify in DB
		currentDateTime = LocalDateTime.now();
		Verify verify = Verify.builder()
				.link(null)
				.otp(randOTP() + "")
				.newPhone(newPhone)
				.expirationTime(currentDateTime.plusMinutes(5))
				.typeOTP(EnumTypeOTP.CHANGEPHONE)
				.account(account.get()).build();

		verifyRepository.save(verify);
		message = messageSource.getMessage("verify.insert-successed", 
				null, LocaleContextHolder.getLocale());
		log.info(message + ": " + verify);
	}
	
	private int randOTP() {
		Random random = new Random();
		int min = 100000;
		int max = 999999;
		int randomNumber = random.nextInt(max - min + 1) + min;
		return randomNumber;
	}

	@Override
	public void verifyChangePhone(String otp) {
		String message = null;
		Optional<Customer> customer = null;
		Optional<Account> account = null;
		Optional<Verify> verify = null;
		LocalDateTime currentDateTime = null;
		UserDetails principal = null;
		
		principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal == null) {
    		message = messageSource.getMessage("account.account-logout", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), 
					"account.account-logout");
    	}
		
		// Found account by email
		customer = customerRepository.findByEmailAndStatus(principal.getUsername(), EnumStatus.ACTIVE);
		account = accountRepository.findByEmailAndStatus(principal.getUsername(), EnumStatus.ACTIVE);
		if (!customer.isPresent() || !account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + principal.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
		
		// Found verify by OTP
		verify = verifyRepository.findByOtpAndTypeOTP(otp, EnumTypeOTP.CHANGEPHONE);
		if ((!verify.isPresent())) {
			message = messageSource.getMessage("verify.otp.otp-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + otp);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.otp.otp-notfound");
		}
		
		// Check type of OTP
		if ((verify.get().getTypeOTP().compareTo(EnumTypeOTP.CHANGEPHONE) == 1)) {
			message = messageSource.getMessage("verify.otp.otp-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + otp + " type " + EnumTypeOTP.CHANGEPHONE.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.otp.otp-notfound");
		}
		
		// Check expired OTP
		currentDateTime = LocalDateTime.now();
		if(verify.get().getExpirationTime().isBefore(currentDateTime)) {
			message = messageSource.getMessage("verify.otp.otp-expired", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + principal.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.otp.otp-expired");
		}
		 
		if(!verify.get().getAccount().equals(account.get())) {
			message = messageSource.getMessage("verify.otp.otp-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + principal.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.otp.otp-invalid");
		}
		
		// Update phone number
		customer.get().setPhone(verify.get().getNewPhone());
		
		customerRepository.save(customer.get());
		message = messageSource.getMessage("account.update-successed", 
				null, LocaleContextHolder.getLocale());
		log.info(message + ": " + customer.get().toString());
		
		// Delete verify
		verifyRepository.deleteById(verify.get().getId());
		message = messageSource.getMessage("verify.delete-successed", 
				null, LocaleContextHolder.getLocale());
		log.info(message + ": " + verify.get().getId());
		
		logoutService.logout();
	}
}
