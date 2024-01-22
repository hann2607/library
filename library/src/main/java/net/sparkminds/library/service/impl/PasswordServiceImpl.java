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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.changeinfo.ChangePassRequest;
import net.sparkminds.library.dto.changeinfo.ConfirmResetPassRequest;
import net.sparkminds.library.dto.changeinfo.ResetPassRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.enumration.EnumTypeOTP;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.repository.VerifyRepository;
import net.sparkminds.library.service.EncryptionService;
import net.sparkminds.library.service.MailService;
import net.sparkminds.library.service.PasswordService;

@Service
@RequiredArgsConstructor
@Log4j2
public class PasswordServiceImpl implements PasswordService{
	
	private final AccountRepository accountRepository;        // Handle entities account
	private final VerifyRepository verifyRepository;
	private final MessageSource messageSource;
	private final EncryptionService encryptionService;
	private final MailService mailService;        // Sending mail

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void resetPassword(ResetPassRequest resetPassRequest) {
		Optional<Account> account = null;
		Verify verify = null;
		String message = null;
		String newPass = null;
		String mailBody = null;        // Content for send mail
		String otp = null;
		LocalDateTime currentDateTime = null;
		
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
		newPass = resetPassRequest.getNewPassword();
		
		if(encryptionService.compare(newPass, account.get().getPassword())) {
			message = messageSource.getMessage("account.newpassword.newpassword-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + resetPassRequest.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.newpassword.newpassword-invalid");
		}
		
		// Update account with new password and set first time login
		currentDateTime = LocalDateTime.now();
		otp = randOTP() + "";
		verify = Verify.builder()
				.otp(otp)
				.newPassword(encryptionService.encrypt(newPass))
				.expirationTime(currentDateTime.plusMinutes(5))
				.typeOTP(EnumTypeOTP.RESETPASS)
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

		// Send mail new password
		try {
			mailBody = readTemplateMailFreemarker(absolutePath, otp);
			mailService.send(account.get().getEmail(), "VERIFY NEW PASSWORD", mailBody);
		} catch (Exception e) {
			message = messageSource
					.getMessage("mail.sendmail.templatefreemarker.templatefreemarker-notgenerated", 
							null, LocaleContextHolder.getLocale());

			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"mail.sendmail.templatefreemarker.templatefreemarker-notgenerated");
		}				
	}
	
	// Method return random OTP
	private int randOTP() {
		Random random = new Random();
		int min = 100000;
		int max = 999999;
		int randomNumber = random.nextInt(max - min + 1) + min;
		return randomNumber;
	}	

	// Method generate mail info
	private String readTemplateMailFreemarker(String absolutePath, String otp) throws IOException {
		Writer out = null; // work with StringWriter
		Map<String, Object> templateData = new HashMap<>(); // data for template freemarker
		String message = null; // message error or success

		try {
			// Load template HTML from file freemarker
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
			cfg.setClassForTemplateLoading(RegisterServiceImpl.class, "/");
			Template template = cfg.getTemplate("/templates/mail/templatemail-resetpass.ftl");

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
	
	@Override
	public void confirmResetPassword(ConfirmResetPassRequest confirmResetPassRequest) {
		Account account = null;
		Optional<Verify> verify = null;
		String message = null;
		
		// Found verify by OTP
		verify = verifyRepository.findByOtpAndTypeOTP(confirmResetPassRequest.getOtp(), EnumTypeOTP.RESETPASS);
		if(!verify.isPresent()) {
			message = messageSource.getMessage("verify.otp.otp-notfound", 
					null, LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(), 
					"verify.otp.otp-notfound");
		}
		
		account = verify.get().getAccount();
		if(!account.getEmail().equals(confirmResetPassRequest.getUsername())) {
			message = messageSource.getMessage("account.email.email-invalid", 
					null, LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), 
					"account.email.email-invalid");
		}
		
		account.setPassword(verify.get().getNewPassword());
		try {
			accountRepository.save(account);
			message = messageSource.getMessage("account.update-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + account.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("account.update-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + account.toString());
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
	public void changePassword(ChangePassRequest changePassRequest) {
		String password = changePassRequest.getPassword();
		String newPass = changePassRequest.getNewPassword();
		String message = null;
		Optional<Account> account = null;
		UserDetails principal = null;
		
		principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.out.println(principal);
		if(principal == null) {
    		message = messageSource.getMessage("account.account-logout", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), 
					"account.account-logout");
    	}

		// Found account by email
		account = accountRepository.findByEmail(principal.getUsername());
		if (!account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + principal.getUsername());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
		
		// Compare password and and password in DB
		if(!encryptionService.compare(password, account.get().getPassword())) {
			message = messageSource.getMessage("account.password.password-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + password);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.password.password-invalid");
		}
		
		// Compare new password and and password in DB
		if(encryptionService.compare(newPass, account.get().getPassword())) {
			message = messageSource.getMessage("account.newpassword.newpassword-invalid", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + newPass);
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
}
