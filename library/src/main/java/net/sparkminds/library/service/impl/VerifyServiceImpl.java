package net.sparkminds.library.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Verify;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.VerifyRepository;
import net.sparkminds.library.service.VerifyService;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class VerifyServiceImpl implements VerifyService {

	private final VerifyRepository verifyRepository;
	private final MessageSource messageSource;

	@Override
	public void save(Verify verify) {
		String message = null;
		
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

	@Override
	public void delete(Long verifyId) {
		String message;
		try {
			verifyRepository.deleteById(verifyId);
			message = messageSource.getMessage("verify.delete-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + verifyId);
		} catch (Exception e) {
			message = messageSource.getMessage("verify.delete-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + verifyId);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"verify.delete-failed");
		}
	}

	@Override
	public Verify findByOtp(String otp) {
		String message;
		Verify verify = null;
		
		verify = verifyRepository.findByOtp(otp);
		if(verify != null) {
			message = messageSource.getMessage("verify.otp.find-successed", 
					null, LocaleContextHolder.getLocale());
			log.info(message + ": " + otp);
		}
		
		return verifyRepository.findByOtp(otp);
	}

	@Override
	public List<Verify> findByAccountId(Long accountId) {
		String message;
		List<Verify> verifies = new ArrayList<>();
		
		verifies = verifyRepository.findByAccountId(accountId);
		if(!verifies.isEmpty()) {
			message = messageSource.getMessage("verify.accountid.find-successed", 
					null, LocaleContextHolder.getLocale());
			log.info(message + ": " + accountId);
			return verifies;
		} else {
			message = messageSource.getMessage("verify.accountid.account-actived", 
					null, LocaleContextHolder.getLocale());
			log.error(message + ": " + accountId);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"verify.accountid.account-actived");
		}
	}
}
