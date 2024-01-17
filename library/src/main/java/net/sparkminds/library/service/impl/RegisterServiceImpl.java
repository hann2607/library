package net.sparkminds.library.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.register.RegisterRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.entity.Role;
import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.enumration.EnumRole;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.enumration.EnumTypeOTP;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.mapper.RegisterRequestMapper;
import net.sparkminds.library.service.AccountService;
import net.sparkminds.library.service.CustomerService;
import net.sparkminds.library.service.EncryptionService;
import net.sparkminds.library.service.MailService;
import net.sparkminds.library.service.RegisterService;
import net.sparkminds.library.service.RoleService;
import net.sparkminds.library.service.VerifyService;

@Service
@RequiredArgsConstructor
@Log4j2
//@Transactional
public class RegisterServiceImpl implements RegisterService {

	private final RegisterRequestMapper userMapper;        // Use convert DTO to Entity
	private final EncryptionService encryptionService;        // Encrypt password
	private final RoleService roleService;        // handle entity Role
	private final AccountService accountService;        // handle entity Account
	private final CustomerService customerService;        // handle entity Account
	private final MessageSource messageSource;        // Get message error from MessageError.properties
	private final MailService mailService;        // Sending mail
	private final VerifyService verifyService;        // Handle entities Verify
	private final String baseUrl = "http://localhost:8080/api/v1/common";

	@Override
	public RegisterRequest register(RegisterRequest userDTO) {
		List<Account> accounts = new ArrayList<>();        // Get accounts by email
		Customer user = null;        // Save User to database
		Role role = null;        // Find role by role name
		String message = null;        // error message in file MessageError.properties
		String otp = null;        // OTP Random 6 number
		String link = null;        // Link verify account
		String fullname = null;        // Get Fullname account
		String mailBody = null;        // Content for send mail
		LocalDateTime currentDateTime = null;        // current date time for set expirationTime otp

		// Get path templateFreemarker email
		Path resourceDirectory = Paths.get("src", "main", "resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();

		// convert DTO --> model
		user = userMapper.dtoToModel(userDTO);

		// Find account by email if not existed -> accept register
		accounts = accountService.findByEmail(user.getEmail());
		if (!accounts.isEmpty()) {
			message = messageSource.getMessage("account.email.email-existed", 
					null, LocaleContextHolder.getLocale());

			log.error(message + ": " + user.toString());
			throw new RequestException(message, HttpStatus.CONFLICT.value(), 
					"account.email.email-existed");
		}

		// Set info user
		role = roleService.findByRole(EnumRole.USER);
		user.setPassword(encryptionService.encrypt(user.getPassword()));
		user.setVerify(false);
		user.setStatus(EnumStatus.ACTIVE);
		user.setLoginAttempt(0);
		user.setFirstTimeLogin(true);
		user.setRole(role);
		
		// Create user
		customerService.create(user);

		// Create otp, link and mail info
		otp = randOTP() + "";        // Generate OTP
		link = generateLink(user.getEmail(), otp);        // Generate link verify
		fullname = userDTO.getLastname() + " " + userDTO.getFirstname();

		// Send mail
		try {
			mailBody = readTemplateMailFreemarker(absolutePath, otp, link, fullname);
			mailService.send(user.getEmail(), "VERIFY ACCOUNT", mailBody);
		} catch (Exception e) {
			message = messageSource
					.getMessage("mail.sendmail.templatefreemarker.templatefreemarker-notgenerated", 
							null, LocaleContextHolder.getLocale());

			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"mail.sendmail.templatefreemarker.templatefreemarker-notgenerated");
		}

		// Setup verify and create
		currentDateTime = LocalDateTime.now();
		Verify verify = Verify.builder()
				.link(link)
				.otp(otp)
				.expirationTime(currentDateTime.plusMinutes(5))
				.typeOTP(EnumTypeOTP.REGISTER)
				.account(user).build();

		verifyService.create(verify);

		return userDTO;
	}

	// Method return random OTP
	private int randOTP() {
		Random random = new Random();
		int min = 100000;
		int max = 999999;
		int randomNumber = random.nextInt(max - min + 1) + min;
		return randomNumber;
	}

	// Method return link verify
	private String generateLink(String username, String otp) {
		String link = baseUrl + "/register/verify/link/" + otp;
		return link;
	}

	// Method generate mail info
	private String readTemplateMailFreemarker(String absolutePath, String otp, String link, String fullname)
			throws IOException {
		Writer out = null;        // work with StringWriter
		Map<String, Object> templateData = new HashMap<>();        // data for template freemarker
		String message = null;        // message error or success

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

			// Convert template freemarker to string
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
}
