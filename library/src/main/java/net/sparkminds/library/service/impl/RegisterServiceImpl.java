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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.transaction.Transactional;
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
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.repository.CustomerRepository;
import net.sparkminds.library.repository.RoleRepository;
import net.sparkminds.library.repository.VerifyRepository;
import net.sparkminds.library.service.EncryptionService;
import net.sparkminds.library.service.MailService;
import net.sparkminds.library.service.RegisterService;

@Service
@RequiredArgsConstructor
@Log4j2
//@Transactional
public class RegisterServiceImpl implements RegisterService {

	private final RegisterRequestMapper userMapper;        // Use convert DTO to Entity
	private final EncryptionService encryptionService;        // Encrypt password
	private final RoleRepository roleRepository;        // handle entity Role
	private final AccountRepository accountRepository;        // handle entity Account
	private final CustomerRepository customerRepository;        // handle entity Account
	private final MessageSource messageSource;        // Get message error from MessageError.properties
	private final MailService mailService;        // Sending mail
	private final VerifyRepository verifyRepository;        // Handle entities Verify
	
	@Value("${baseUrlCommon}")
	private String baseUrlCommon;

	@Override
	@Transactional(rollbackOn = Exception.class)
	public RegisterRequest register(RegisterRequest userDTO) {
		Optional<Account> account = null;        // Get accounts by email
		Customer customer = null;        // Save User to database
		Optional<Role> role = null;        // Find role by role name
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
		customer = userMapper.dtoToModel(userDTO);

		// Find account by email if not existed -> accept register
		account = accountRepository.findByEmail(customer.getEmail());
		if(account.isPresent()) {
			message = messageSource.getMessage("account.email.email-existed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + customer.getEmail());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-existed");
		}

		// Set info user
		role = roleRepository.findByRole(EnumRole.ROLE_USER);
		if(!role.isPresent()) {
			message = messageSource
					.getMessage("role.role-notfound", 
							null, LocaleContextHolder.getLocale());

			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"role.role-notfound");
		}
		
		customer.setPassword(encryptionService.encrypt(customer.getPassword()));
		customer.setVerify(false);
		customer.setStatus(EnumStatus.ACTIVE);
		customer.setLoginAttempt(0);
		customer.setFirstTimeLogin(true);
		customer.setRole(role.get());
		
		// Create user
		try {
			customerRepository.save(customer);
			message = messageSource.getMessage("account.insert-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + customer.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("account.insert-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + customer.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.insert-failed");
		}

		// Create otp, link and mail info
		otp = randOTP() + "";        // Generate OTP
		link = generateLink(customer.getEmail(), otp);        // Generate link verify
		fullname = userDTO.getLastname() + " " + userDTO.getFirstname();

		// Send mail
		try {
			mailBody = readTemplateMailFreemarker(absolutePath, otp, link, fullname);
			mailService.send(customer.getEmail(), "VERIFY ACCOUNT", mailBody);
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
				.account(customer).build();

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
		String link = baseUrlCommon + "/register/verify/link/" + otp;
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
