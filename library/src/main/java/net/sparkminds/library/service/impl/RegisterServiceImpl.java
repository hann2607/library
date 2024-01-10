package net.sparkminds.library.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.register.RegisterRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.User;
import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.enums.EnumStatus;
import net.sparkminds.library.enums.EnumTypeOTP;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.mapper.RegisterRequestMapper;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.repository.UserRepository;
import net.sparkminds.library.service.AccountService;
import net.sparkminds.library.service.EncryptionService;
import net.sparkminds.library.service.MailService;
import net.sparkminds.library.service.RegisterService;
import net.sparkminds.library.service.VerifyService;

@Service
@RequiredArgsConstructor
@Log4j2
public class RegisterServiceImpl implements RegisterService {

	private final UserRepository userRepository; // Query entity User
	private final AccountRepository accountRepository; // Query entity User
	private final RegisterRequestMapper userMapper; // Use convert DTO to Entity
	private final EncryptionService encryptionService; // Encrypt password
	private final AccountService accountService; // Query entity Account
	private final MessageSource messageSource; // Get message error from MessageError.properties
	private final MailService mailService; // Sending mail
	private final VerifyService verifyService; // Handle entities Verify
	private final String baseUrl = "http://localhost:8080/api/v1/common";

	@Override
	public ResponseEntity<RegisterRequest> register(RegisterRequest userDTO) {
		List<Account> accounts = new ArrayList<>(); // Get accounts by email
		User user = null; // Save User to database
		String message = null; // error message in file MessageError.properties
		String otp = null; // OTP Random 6 number
		String link = null; // Link verify account
		String fullname = null; // Get Fullname account
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
			userRepository.save(user);

			message = messageSource.getMessage("Insert.Success.Account", null, LocaleContextHolder.getLocale());
			log.info(message + ": " + user);

			// Send mail Verify
			try {
				otp = randOTP() + ""; // Generate OTP
				link = generateLink(user.getEmail(), otp); // Generate link verify
				fullname = userDTO.getLastname() + " " + userDTO.getFirstname();
				mailBody = readTemplateMailFreemarker(absolutePath, otp, link, fullname);

				mailService.send(user.getEmail(), "VERIFY ACCOUNT", mailBody);

				currentDateTime = LocalDateTime.now();
				Verify verify = Verify.builder().link(link).otp(otp).expirationTime(currentDateTime.plusMinutes(5))
						.typeOTP(EnumTypeOTP.REGISTER).account(user).build();

				verifyService.save(verify);

				return ResponseEntity.ok(userDTO);
			} catch (Exception e) {
				message = messageSource.getMessage("SendMail.Error.Mail", null, LocaleContextHolder.getLocale());
				log.error(message + ": " + user);
				throw new RequestException(message);
			}
		} else {
			message = messageSource.getMessage("Duplicate.Account", null, LocaleContextHolder.getLocale());
			log.error(message + ": " + user);
			throw new RequestException(message);
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

	private String readTemplateMailFreemarker(String absolutePath, String otp, String link, String fullname)
			throws IOException {
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
			message = messageSource.getMessage("SendMail.Error.Mail.TemplateFreemarker", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	public void verifyAccountByLink(String otp) {
		Verify verify = null;
		LocalDateTime currentDateTime = null;
		Account account = null;
		String message = null;
		Long verifyId = null;

		verify = verifyService.findByOtp(otp);
		verifyId = verify.getId();

		if (verify != null) {
			currentDateTime = LocalDateTime.now();
			long seconds = ChronoUnit.SECONDS.between(currentDateTime, verify.getExpirationTime());

			// Check time link
			if (seconds > 0) {
				account = verify.getAccount();
				account.setVerify(true);

				// Update isVerify account
				accountRepository.save(verify.getAccount());

				// Delete verify
				verifyService.delete(verifyId);
			} else {
				verifyService.delete(verifyId);
				message = messageSource.getMessage("ExpirationTime.Error.Verify.Link", null,
						LocaleContextHolder.getLocale());
				log.error(message);
				throw new RequestException(message);
			}
		}
	}

	@Override
	public void verifyAccountByOTP(String otp) {
		Verify verify = null;
		LocalDateTime currentDateTime = null;
		Account account = null;
		String message = null;
		Long verifyId = null;

		verify = verifyService.findByOtp(otp);
		verifyId = verify.getId();

		if (verify != null) {
			currentDateTime = LocalDateTime.now();
			long seconds = ChronoUnit.SECONDS.between(currentDateTime, verify.getExpirationTime());

			// Check time link
			if (seconds > 0) {
				account = verify.getAccount();
				account.setVerify(true);

				// Update isVerify account
				accountRepository.save(verify.getAccount());

				// Delete verify
				verifyService.delete(verifyId);
			} else {
				verifyService.delete(verifyId);
				message = messageSource.getMessage("ExpirationTime.Error.Verify.Otp", null,
						LocaleContextHolder.getLocale());
				log.error(message);
				throw new RequestException(message);
			}
		}
	}

	@Override
	public void resendOtpAndLink(String email) {
		Account account = null;
		
	}
}
