package net.sparkminds.library.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.changeinfo.ChangeEmailRequest;
import net.sparkminds.library.dto.changeinfo.ChangePassRequest;
import net.sparkminds.library.dto.changeinfo.ChangePhoneRequest;
import net.sparkminds.library.dto.changeinfo.ResetPassRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.entity.Session;
import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.enumration.EnumTypeOTP;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.jwt.JwtUtil;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.repository.CustomerRepository;
import net.sparkminds.library.repository.SessionRepository;
import net.sparkminds.library.repository.VerifyRepository;
import net.sparkminds.library.service.ChangeInfoService;
import net.sparkminds.library.service.EncryptionService;
import net.sparkminds.library.service.LogoutService;
import net.sparkminds.library.service.MailService;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChangeInfoServiceImpl implements ChangeInfoService {

	private final AccountRepository accountRepository;        // Handle entities account
	private final CustomerRepository customerRepository;        // Handle entities customer
	private final VerifyRepository verifyRepository;        // Handle entities verify
	private final MessageSource messageSource;
	private final EncryptionService encryptionService;
	private final MailService mailService;        // Sending mail
	private final SessionRepository sessionRepository;        // Handle entities session
	private final LogoutService logoutService;
	private final JwtUtil jwtUtil;

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void resetPassword(ResetPassRequest resetPassRequest) {
		Optional<Account> account = null;
		String message = null;
		String newPass = null;
		String mailBody = null;        // Content for send mail
		
		// Get path templateFreemarker email
		Path resourceDirectory = Paths.get("src", "main", "resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();

		// Found account by email
		account = accountRepository.findByEmail(resetPassRequest.getUsername());
		if (!account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + resetPassRequest.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}

		// Generated new password random and check with in DB 
		newPass = generateRandomPassword();
		while (encryptionService.compare(newPass, account.get().getPassword())) {
			newPass = generateRandomPassword();
		}
		
		// Update account with new password and set first time login
		account.get().setPassword(encryptionService.encrypt(newPass));
		account.get().setFirstTimeLogin(true);
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

		// Send mail new password
		try {
			mailBody = readTemplateMailFreemarker(absolutePath, newPass);
			mailService.send(account.get().getEmail(), "NEW PASSWORD", mailBody);
		} catch (Exception e) {
			message = messageSource
					.getMessage("mail.sendmail.templatefreemarker.templatefreemarker-notgenerated", 
							null, LocaleContextHolder.getLocale());

			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"mail.sendmail.templatefreemarker.templatefreemarker-notgenerated");
		}

	}

	private String generateRandomPassword() {
		StringBuilder password = new StringBuilder();
		String characters = "";
		String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
		String DIGITS = "0123456789";
		String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+";

		characters += UPPER_CASE;
		characters += LOWER_CASE;
		characters += DIGITS;
		characters += SPECIAL_CHARACTERS;

		Random random = new SecureRandom();

		for (int i = 0; i < 32; i++) {
			int randomIndex = random.nextInt(characters.length());
			password.append(characters.charAt(randomIndex));
		}

		return password.toString();
	}

	// Method generate mail info
	private String readTemplateMailFreemarker(String absolutePath, String newPass) throws IOException {
		Writer out = null; // work with StringWriter
		Map<String, Object> templateData = new HashMap<>(); // data for template freemarker
		String message = null; // message error or success

		try {
			// Load template HTML from file freemarker
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
			cfg.setClassForTemplateLoading(RegisterServiceImpl.class, "/");
			Template template = cfg.getTemplate("/templatemail-resetpass.ftl");

			// Fill data into the template
			templateData.put("newPass", newPass);

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

	@Override
	public void changePassword(ChangePassRequest changePassRequest) {
		String password = changePassRequest.getPassword();
		String newPass = changePassRequest.getNewPassword();
		String message = null;
		Optional<Account> account = null;
		
		// Found account by email
		account = accountRepository.findByEmail(changePassRequest.getUsername());
		if (!account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + changePassRequest.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
		
		// Compare password and and password in DB
		if(!encryptionService.compare(password, account.get().getPassword())) {
			message = messageSource.getMessage("account.password.password-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + changePassRequest.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.password.password-invalid");
		}
		
		// Compare new password and and password in DB
		if(encryptionService.compare(newPass, account.get().getPassword())) {
			message = messageSource.getMessage("account.newpassword.newpassword-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + changePassRequest.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.newpassword.newpassword-invalid");
		}
		
		// Update account with new password
		account.get().setFirstTimeLogin(false);
		account.get().setPassword(encryptionService.encrypt(newPass));
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
	}

	@Override
	public void changePhone(ChangePhoneRequest changePhoneRequest) {
		String newPhone = changePhoneRequest.getNewPhone();
		String message = null;
		Optional<Customer> customer = null;
		Optional<Account> account = null;
		LocalDateTime currentDateTime = null;
		String email = getEmailBySession();
		
		// Found account by email
		customer = customerRepository.findByEmail(email);
		account = accountRepository.findByEmail(email);
		if (!customer.isPresent() || !account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + email);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
		
		// Compare new phone with phone in DB
		if (customer.get().getPhone().equals(newPhone)) {
			message = messageSource.getMessage("account.newphone.newphone-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + email);
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

		try {
			verifyRepository.save(verify);
			message = messageSource.getMessage("verify.insert-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + verify);
		} catch (Exception e) {
			message = messageSource.getMessage("verify.insert-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + verify);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.insert-failed");
		}
	}
	
	private int randOTP() {
		Random random = new Random();
		int min = 100000;
		int max = 999999;
		int randomNumber = random.nextInt(max - min + 1) + min;
		return randomNumber;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void verifyChangePhone(String otp) {
		String message = null;
		Optional<Customer> customer = null;
		Optional<Account> account = null;
		Optional<Verify> verify = null;
		LocalDateTime currentDateTime = null;
		String email = getEmailBySession();
		
		// Found account by email
		customer = customerRepository.findByEmail(email);
		account = accountRepository.findByEmail(email);
		if (!customer.isPresent() || !account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + email);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
		
		// Found verify by OTP
		verify = verifyRepository.findByOtp(otp);
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

			log.error(message + ": " + email);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.otp.otp-expired");
		}
		 
		if(!verify.get().getAccount().equals(account.get())) {
			message = messageSource.getMessage("verify.otp.otp-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + email);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.otp.otp-invalid");
		}
		
		// Update phone number
		customer.get().setPhone(verify.get().getNewPhone());
		try {
			customerRepository.save(customer.get());
			message = messageSource.getMessage("account.update-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + customer.get().toString());
		} catch (Exception e) {
			message = messageSource.getMessage("account.update-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + customer.get().toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.update-failed");
		}
		
		// Delete verify
		try {
			verifyRepository.deleteById(verify.get().getId());
			message = messageSource.getMessage("verify.delete-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + verify.get().getId());
		} catch (Exception e) {
			message = messageSource.getMessage("verify.delete-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + verify.get().getId());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.delete-failed");
		}
	}

	@Override
	public void changeEmail(ChangeEmailRequest changeEmailRequest) {
		String newEmail = changeEmailRequest.getNewEmail();
		String message = null;
		Optional<Customer> customer = null;
		Optional<Account> account = null;
		LocalDateTime currentDateTime = null;
		String mailBody = null;        // Content for send mail
		String otp = null;
		String email = getEmailBySession();
		
		// Get path templateFreemarker email
		Path resourceDirectory = Paths.get("src", "main", "resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();
		
		// Found account by email
		customer = customerRepository.findByEmail(email);
		account = accountRepository.findByEmail(email);
		if (!customer.isPresent() || !account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + email);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
		
		// Compare new email with email in DB
		if (customer.get().getEmail().equals(newEmail)) {
			message = messageSource.getMessage("account.newemail.newemail-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + email);
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

		try {
			verifyRepository.save(verify);
			message = messageSource.getMessage("verify.insert-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + verify);
		} catch (Exception e) {
			
			message = messageSource.getMessage("verify.insert-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + verify + e);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.insert-failed");
		}
		
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
	@Transactional(rollbackOn = Exception.class)
	public void verifyChangeEmail(String otp) {
		String message = null;
		Optional<Customer> customer = null;
		Optional<Account> account = null;
		Optional<Verify> verify = null;
		LocalDateTime currentDateTime = null;
		String email = getEmailBySession();
		
		// Found account by email
		customer = customerRepository.findByEmail(email);
		account = accountRepository.findByEmail(email);
		if (!customer.isPresent() || !account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + email);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
		
		// Found verify by OTP
		verify = verifyRepository.findByOtp(otp);
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

			log.error(message + ": " + email);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.otp.otp-expired");
		}
		
		if(!verify.get().getAccount().equals(account.get())) {
			message = messageSource.getMessage("verify.otp.otp-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + email);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.otp.otp-invalid");
		}
		
		// Update email 
		customer.get().setEmail(verify.get().getNewEmail());
		try {
			customerRepository.save(customer.get());
			message = messageSource.getMessage("account.update-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + customer.get().toString());
		} catch (Exception e) {
			message = messageSource.getMessage("account.update-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + customer.get().toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.update-failed");
		}
		
		// Delete verify 
		try {
			verifyRepository.deleteById(verify.get().getId());
			message = messageSource.getMessage("verify.delete-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + verify.get().getId());
		} catch (Exception e) {
			message = messageSource.getMessage("verify.delete-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + verify.get().getId());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.delete-failed");
		}
		
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
			Template template = cfg.getTemplate("/templatemail-changemail.ftl");

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
	
	private String getEmailBySession() {
		String message = null;
		String authHeader = null;
		String token = null;
		String JTI = null;
		HttpServletRequest request = null;
		Optional<Session> session = null;
		
		request = ((ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes()).getRequest();
		
        authHeader = request.getHeader("Authorization");
        if(authHeader == null) {
        	message = messageSource.getMessage("account.account-logouterror", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), 
					"account.account-logouterror");
        }  
        
        token = authHeader.substring(7);
    	JTI = jwtUtil.extractJTI(token);
        
        session = sessionRepository.findByJti(JTI);
        if(!session.isPresent()) {
        	message = messageSource.getMessage("session.session.expired", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), 
					"session.session.expired");
        }
        
        return session.get().getAccount().getEmail();
	}
}
