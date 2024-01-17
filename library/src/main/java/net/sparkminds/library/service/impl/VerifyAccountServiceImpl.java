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
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.enumration.EnumTypeOTP;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.service.AccountService;
import net.sparkminds.library.service.CustomerService;
import net.sparkminds.library.service.MailService;
import net.sparkminds.library.service.VerifyAccountService;
import net.sparkminds.library.service.VerifyService;

@Service
@RequiredArgsConstructor
@Log4j2
public class VerifyAccountServiceImpl implements VerifyAccountService {

	private final AccountService accountService; // handle entity Account
	private final CustomerService customerService; // handle entity Account
	private final MessageSource messageSource; // Get message error from MessageError.properties
	private final MailService mailService; // Sending mail
	private final VerifyService verifyService; // Handle entities Verify
	private final String baseUrl = "http://localhost:8080/api/v1/common";

	@Override
	public void verifyAccountByLink(String otp) {
		Verify verify = null; // model verify
		LocalDateTime currentDateTime = null; // current date time using check expirationTime
		Customer user = null; // model user
		String message = null; // message error or success
		Long verifyId = null; // verify id, if verify success --> delete by Id

		// Find verify by OTP(Link and OTP is one row in DB)
		verify = verifyService.findByOtp(otp);
		if (verify == null) {
			message = messageSource.getMessage("verify.link.link-notfound", null, LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(), "verify.link.link-notfound");
		}

		verifyId = verify.getId();
		currentDateTime = LocalDateTime.now();
		long seconds = Duration.between(currentDateTime, verify.getExpirationTime()).getSeconds();

		// Check time link
		if (seconds <= 0) {
			// Delete verify
			verifyService.delete(verifyId);
			message = messageSource.getMessage("verify.link.link-expired", null, LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "verify.link.link-expired");
		}

		user = customerService.findById(verify.getAccount().getId());
		if (user == null) {
			message = messageSource.getMessage("user.id.user-notexisted", null, LocaleContextHolder.getLocale());

			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "user.id.user-notexisted");
		}

		user.setVerify(true);

		// Update isVerify account
		customerService.update(user);

		// Delete verify
		verifyService.delete(verifyId);
	}

	@Override
	public void verifyAccountByOTP(String otp) {
		Verify verify = null; // model verify
		LocalDateTime currentDateTime = null; // current date time using check expirationTime
		Customer user = null; // model user
		String message = null; // message error or success
		Long verifyId = null; // verify id, if verify success --> delete by Id

		// Find verify by OTP(Link and OTP is one row in DB)
		verify = verifyService.findByOtp(otp);
		if (verify == null) {
			message = messageSource.getMessage("verify.otp.otp-notfound", null, LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(), "verify.otp.otp-notfound");
		}

		verifyId = verify.getId();
		currentDateTime = LocalDateTime.now();
		long seconds = Duration.between(currentDateTime, verify.getExpirationTime()).getSeconds();

		// Check time link
		if (seconds <= 0) {
			// Delete verify
			verifyService.delete(verifyId);
			message = messageSource.getMessage("verify.otp.otp-expired", null, LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "verify.otp.otp-expired");
		}

		user = customerService.findById(verify.getAccount().getId());
		if (user == null) {
			message = messageSource.getMessage("user.id.user-notexisted", null, LocaleContextHolder.getLocale());

			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "user.id.user-notexisted");
		}

		user.setVerify(true);

		// Update isVerify account
		customerService.update(user);

		// Delete verify
		verifyService.delete(verifyId);
	}

	@Override
	public void resendOtpAndLink(String email) {
		Customer user = null; // model user
		Account account = null; // model account
		Verify verify = null; // model verify
		List<Verify> verifies = new ArrayList<>(); // Get list verify by account
		LocalDateTime currentDateTime = LocalDateTime.now();
		; // current date time for check expiration time
		String otp = null; // OTP random
		String link = null; // generate link
		String message = null; // message error or success
		String fullname = null; // fullname of user for send mail
		String mailBody = null; // mail info

		// path template mail freemarker
		Path resourceDirectory = Paths.get("src", "main", "resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();

		// find user and account
		user = customerService.findByEmail(email);
		account = accountService.findByEmail(email).get(0);

		// check user and account != null --> verify
		if (user == null || account == null) {
			message = messageSource.getMessage("account.email.email-notfound", null, LocaleContextHolder.getLocale());

			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "account.email.email-notfound");
		}

		otp = randOTP() + ""; // Generate OTP
		link = generateLink(email, otp); // Generate link verify
		fullname = user.getLastname() + " " + user.getFirstname(); // fullname of user

		try {
			// mail info
			mailBody = readTemplateMailFreemarker(absolutePath, otp, link, fullname);
		} catch (IOException e) {
			message = messageSource.getMessage("mail.resend.templatefreemarker.templatefreemarker-notgenerated", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"mail.resend.templatefreemarker.templatefreemarker-notgenerated");
		}

		// get list verifies by account
		verifies = verifyService.findByAccountId(user.getId());
		for (Verify v : verifies) {

			// check verify has type 'REGISTER'
			if (v.getTypeOTP().compareTo(EnumTypeOTP.REGISTER) == 0) {
				verify = v;
			}
		}

		// if verify != null -> update verify
		if (verify != null) {
			verify.setExpirationTime(currentDateTime.plusMinutes(5));
			verify.setLink(link);
			verify.setOtp(otp);

			// update verify
			verifyService.update(verify);
		} else {
			// if verify == null -> create new verify
			verify = Verify.builder().link(link).otp(otp).expirationTime(currentDateTime.plusMinutes(5))
					.typeOTP(EnumTypeOTP.REGISTER).account(account).build();

			verifyService.create(verify);
		}

		// send mail
		try {
			mailService.send(user.getEmail(), "VERIFY ACCOUNT", mailBody);
			message = messageSource.getMessage("verify.resend.resend-successed", null, LocaleContextHolder.getLocale());

			log.info(message);
		} catch (MessagingException e) {
			message = messageSource.getMessage("mail.resend.resend-failed", null, LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "mail.resend.resend-failed");
		}
	}

	// Method generate mail info
	private String readTemplateMailFreemarker(String absolutePath, String otp, String link, String fullname)
			throws IOException {
		Writer out = null; // work with StringWriter
		Map<String, Object> templateData = new HashMap<>(); // data for template freemarker
		String message = null; // message error or success

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
			message = messageSource.getMessage("mail.templatefreemarker.templatefreemarker-notgenerated", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"mail.templatefreemarker.templatefreemarker-notgenerated");
		} finally {
			out.close();
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

	// Method return link verify
	private String generateLink(String username, String otp) {
		String link = baseUrl + "/register/verify/link/" + otp;
		return link;
	}
}
