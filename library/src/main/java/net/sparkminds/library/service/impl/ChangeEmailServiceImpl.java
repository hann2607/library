package net.sparkminds.library.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.changeinfo.ChangeEmailRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.enumration.EnumTypeOTP;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.repository.CustomerRepository;
import net.sparkminds.library.repository.VerifyRepository;
import net.sparkminds.library.service.ChangeEmailService;
import net.sparkminds.library.service.LogoutService;
import net.sparkminds.library.service.MailService;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChangeEmailServiceImpl implements ChangeEmailService {

	private final AccountRepository accountRepository;        // Handle entities account
	private final CustomerRepository customerRepository;        // Handle entities customer
	private final VerifyRepository verifyRepository;        // Handle entities verify
	private final MessageSource messageSource;
	private final MailService mailService;        // Sending mail
	private final LogoutService logoutService;

	@Override
	public void changeEmail(ChangeEmailRequest changeEmailRequest) {
		String newEmail = changeEmailRequest.getNewEmail();
		String message = null;
		Optional<Customer> customer = null;
		Optional<Account> account = null;
		LocalDateTime currentDateTime = null;
		String mailBody = null;        // Content for send mail
		String otp = null;
		UserDetails principal = null;
		
		// Get path templateFreemarker email
		Path resourceDirectory = Paths.get("src", "main", "resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();
		
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
		
		// Compare new email with email in DB
		if (customer.get().getEmail().equals(newEmail)) {
			message = messageSource.getMessage("account.newemail.newemail-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + principal.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.newemail.newemail-invalid");
		}
		
		// Generated random OTP and update create verify
		otp = randOTP() + "";
		currentDateTime = LocalDateTime.now();
		Verify verify = Verify.builder()
				.link(null)
				.otp(otp)
				.newEmail(newEmail)
				.expirationTime(currentDateTime.plusMinutes(5))
				.typeOTP(EnumTypeOTP.CHANGEEMAIL)
				.account(account.get()).build();

		verifyRepository.save(verify);
		message = messageSource.getMessage("verify.insert-successed", 
				null, LocaleContextHolder.getLocale());
		log.info(message + ": " + verify);
		
		// Send mail
		try {
			mailBody = readTemplateMailFreemarkerChangeMail(absolutePath, otp);
			mailService.send(newEmail, "CHANGE EMAIL", mailBody);
		} catch (Exception e) {
			message = messageSource
					.getMessage("mail.sendmail.templatefreemarker.templatefreemarker-notgenerated", 
							null, LocaleContextHolder.getLocale());

			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"mail.sendmail.templatefreemarker.templatefreemarker-notgenerated");
		}			
	}

	@Override
	public void verifyChangeEmail(String otp) {
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
		verify = verifyRepository.findByOtpAndTypeOTP(otp, EnumTypeOTP.CHANGEEMAIL);
		if ((!verify.isPresent())) {
			message = messageSource.getMessage("verify.otp.otp-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + otp);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.otp.otp-notfound");
		}
		
		// Check type of OTP
		if ((verify.get().getTypeOTP().compareTo(EnumTypeOTP.CHANGEEMAIL) == 1)) {
			message = messageSource.getMessage("verify.otp.otp-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + otp + " type " + EnumTypeOTP.CHANGEEMAIL.toString());
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
		
		// Update email 
		customer.get().setEmail(verify.get().getNewEmail());
		
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
	
	// Method generate mail info
	private String readTemplateMailFreemarkerChangeMail(String absolutePath, String otp) throws IOException {
		Writer out = null; // work with StringWriter
		Map<String, Object> templateData = new HashMap<>(); // data for template freemarker
		String message = null; // message error or success

		try {
			// Load template HTML from file freemarker
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
			cfg.setClassForTemplateLoading(RegisterServiceImpl.class, "/");
			Template template = cfg.getTemplate("/templates/mail/templatemail-changemail.ftl");

			// Fill data into the template
			templateData.put("otp", otp);

			out = new StringWriter();
			template.process(templateData, out);

			// Convert template freemarker to string
			String result = out.toString();
			return result;
		} catch (Exception e) {
			message = messageSource.getMessage("mail.templatefreemarker.templatefreemarker-notgenerated", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"mail.templatefreemarker.templatefreemarker-notgenerated");
		} finally {
			out.close();
		}
	}	
	
	private int randOTP() {
		Random random = new Random();
		int min = 100000;
		int max = 999999;
		int randomNumber = random.nextInt(max - min + 1) + min;
		return randomNumber;
	}
}
