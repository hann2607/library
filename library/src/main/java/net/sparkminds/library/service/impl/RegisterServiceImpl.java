package net.sparkminds.library.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.register.RegisterRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.User;
import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.enumration.EnumTypeOTP;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.mapper.RegisterRequestMapper;
import net.sparkminds.library.service.AccountService;
import net.sparkminds.library.service.EncryptionService;
import net.sparkminds.library.service.MailService;
import net.sparkminds.library.service.RegisterService;
import net.sparkminds.library.service.UserService;
import net.sparkminds.library.service.VerifyService;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class RegisterServiceImpl implements RegisterService {

	private final RegisterRequestMapper userMapper;        // Use convert DTO to Entity
	private final EncryptionService encryptionService;        // Encrypt password
	private final AccountService accountService;        // handle entity Account
	private final UserService userService;        // handle entity Account
	private final MessageSource messageSource;        // Get message error from MessageError.properties
	private final MailService mailService;        // Sending mail
	private final VerifyService verifyService;        // Handle entities Verify
	private final String baseUrl = "http://localhost:8080/api/v1/common";

	@Override
	public ResponseEntity<RegisterRequest> register(RegisterRequest userDTO) {
		List<Account> accounts = new ArrayList<>();        // Get accounts by email
		User user = null;        // Save User to database
		String message = null;        // error message in file MessageError.properties
		String otp = null;        // OTP Random 6 number
		String link = null;        // Link verify account
		String fullname = null;        // Get Fullname account
		String mailBody = null;
		LocalDateTime currentDateTime = null;
		Path resourceDirectory = Paths.get("src", "main", "resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();

		user = userMapper.dtoToModel(userDTO);
		accounts = accountService.findByEmail(user.getEmail());
		
		if (accounts.isEmpty()) {
			user.setPassword(encryptionService.encrypt(user.getPassword()));
			user.setVerify(false);
			user.setStatus(EnumStatus.ACTIVE);
			userService.save(user);

			// Send mail Verify
			otp = randOTP() + "";        // Generate OTP
			link = generateLink(user.getEmail(), otp);        // Generate link verify
			fullname = userDTO.getLastname() + " " + userDTO.getFirstname();
			
			try {
				mailBody = readTemplateMailFreemarker(absolutePath, otp, link, fullname);
				mailService.send(user.getEmail(), "VERIFY ACCOUNT", mailBody);
			} catch (Exception e) {
				message = messageSource.getMessage("mail.sendmail.templatefreemarker.templatefreemarker-notgenerated", 
						null, LocaleContextHolder.getLocale());
				
				log.error(message);
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"mail.sendmail.templatefreemarker.templatefreemarker-notgenerated");
			}

			currentDateTime = LocalDateTime.now();
			Verify verify = Verify.builder()
					.link(link)
					.otp(otp)
					.expirationTime(currentDateTime.plusMinutes(5))
					.typeOTP(EnumTypeOTP.REGISTER).account(user).build();

			verifyService.save(verify);

			return ResponseEntity.ok(userDTO);

		} else {
			message = messageSource.getMessage("account.email.email-existed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + user.toString());
			throw new RequestException(message, HttpStatus.CONFLICT.value(),
					"account.email.email-existed");
		}
	}

	private int randOTP() {
		Random random = new Random();
		int min = 100000;
		int max = 999999;
		int randomNumber = random.nextInt(max - min + 1) + min;
		return randomNumber;
	}

	private String generateLink(String username, String otp) {
		String link = baseUrl + "/register/verify/link/" + otp;
		return link;
	}

	private String readTemplateMailFreemarker(String absolutePath, String otp, 
			String link, String fullname) throws IOException{
		Writer out = null;
		Map<String, Object> templateData = new HashMap<>();
		String message = null;

		try {
			// Load template HTML from file freemarker
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
			cfg.setClassForTemplateLoading(RegisterServiceImpl.class, "/");
			Template template = cfg.getTemplate("/templatemail.ftl");

			// Fill data into the template
			templateData.put("otp", otp);
			templateData.put("link", link);
			templateData.put("fullname", fullname);

			out = new StringWriter();
			template.process(templateData, out);

			String result = out.toString();
			return result;
		} catch (Exception e) {
			message = messageSource.getMessage("mail.templatefreemarker.templatefreemarker-notgenerated", 
					null, LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"mail.templatefreemarker.templatefreemarker-notgenerated");
		} finally {
			out.close();
		}
	}

	@Override
	public void verifyAccountByLink(String otp) {
		Verify verify = null;
		LocalDateTime currentDateTime = null;
		User user = null;
		String message = null;
		Long verifyId = null;

		verify = verifyService.findByOtp(otp);
		if(verify != null) {
			verifyId = verify.getId();

			currentDateTime = LocalDateTime.now();
			long seconds = Duration.between(currentDateTime, verify.getExpirationTime()).getSeconds();

			// Check time link
			if (seconds > 0) {
				user = userService.findById(verify.getAccount().getId());
				if(user != null) {
					user.setVerify(true);
					
					// Update isVerify account
					userService.save(user);

					// Delete verify
					verifyService.delete(verifyId);
				} else {
					message = messageSource.getMessage("user.id.user-notexisted", 
							null, LocaleContextHolder.getLocale());
					
					log.error(message);
					throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
							"user.id.user-notexisted");
				}
			} else {
				message = messageSource.getMessage("verify.link.link-expired", null,
						LocaleContextHolder.getLocale());
				log.error(message);
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"verify.link.link-expired");
			}
		} else {
			message = messageSource.getMessage("verify.link.link-notfound", 
					null, LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"verify.link.link-notfound");
		}
	}

	@Override
	public void verifyAccountByOTP(String otp) {
		Verify verify = null;
		LocalDateTime currentDateTime = null;
		User user = null;
		String message = null;
		Long verifyId = null;

		verify = verifyService.findByOtp(otp);
		if(verify != null) {
			verifyId = verify.getId();

			currentDateTime = LocalDateTime.now();
			long seconds = Duration.between(currentDateTime, verify.getExpirationTime()).getSeconds();

			// Check time link
			if (seconds > 0) {
				user = userService.findById(verify.getAccount().getId());
				
				if(user != null) {
					user.setVerify(true);

					// Update isVerify account
					userService.save(user);

					// Delete verify
					verifyService.delete(verifyId);
				} else {
					message = messageSource.getMessage("verify.link.link-expired", null,
							LocaleContextHolder.getLocale());
					log.error(message);
					throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
							"verify.link.link-expired");
				}
				
			} else {
				message = messageSource.getMessage("verify.otp.otp-expired", null,
						LocaleContextHolder.getLocale());
				log.error(message);
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"verify.otp.otp-expired");
			}
		} else {
			message = messageSource.getMessage("verify.otp.otp-notfound", 
					null, LocaleContextHolder.getLocale());
			log.error(message + ": " + otp);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"verify.otp.otp-notfound");
		}
		
	}

	@Override
	public void resendOtpAndLink(String email) {
		User user = null;
		Account account = null;
		Verify verify = null;
		List<Verify> verifies = new ArrayList<>();
		LocalDateTime currentDateTime = LocalDateTime.now();
		String otp = null;
		String link = null;
		String message = null;
		String fullname = null;
		String mailBody = null;
		Path resourceDirectory = Paths.get("src", "main", "resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();

		user = userService.findByEmail(email);
		account = accountService.findByEmail(email).get(0);
		
		if(user != null && account != null) {
			verifies = verifyService.findByAccountId(user.getId());
			for (Verify v : verifies) {
				System.out.println(v.getTypeOTP().compareTo(EnumTypeOTP.REGISTER));
				if (v.getTypeOTP().compareTo(EnumTypeOTP.REGISTER) == 0) {
					verify = v;
				}
			}
			
			otp = randOTP() + "";        // Generate OTP
			link = generateLink(email, otp);        // Generate link verify
			fullname = user.getLastname() + " " + user.getFirstname();
			
			try {
				mailBody = readTemplateMailFreemarker(absolutePath, otp, link, fullname);
			} catch (IOException e) {
				message = messageSource.getMessage("mail.resend.templatefreemarker.templatefreemarker-notgenerated", 
						null, LocaleContextHolder.getLocale());
				log.error(message);
				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
						"mail.resend.templatefreemarker.templatefreemarker-notgenerated");
			}

			if (verify != null) {
				verify.setExpirationTime(currentDateTime.plusMinutes(5));
				verify.setLink(link);
				verify.setOtp(otp);

				verifyService.save(verify);
				try {
					mailService.send(user.getEmail(), "VERIFY ACCOUNT", mailBody);
				} catch (MessagingException e) {
					message = messageSource.getMessage("mail.resend.resend-failed", null,
							LocaleContextHolder.getLocale());
					log.error(message);
					throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
							"mail.resend.resend-failed");
				}
			} else {
				verify = Verify.builder().link(link).otp(otp).expirationTime(currentDateTime.plusMinutes(5))
						.typeOTP(EnumTypeOTP.REGISTER).account(account).build();

				verifyService.save(verify);
				try {
					mailService.send(user.getEmail(), "VERIFY ACCOUNT", mailBody);
				} catch (MessagingException e) {
					message = messageSource.getMessage("mail.resend.resend-failed", null,
							LocaleContextHolder.getLocale());
					log.error(message);
					throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
							"mail.resend.resend-failed");
				}
			}

			message = messageSource.getMessage("verify.resend.resend-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message);
		} else {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}	
	}
}
