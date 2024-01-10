package net.sparkminds.library.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
		String message;
		try {
			verifyRepository.save(verify);
			message = messageSource.getMessage("Insert.Success.Verify", null, LocaleContextHolder.getLocale());
			log.info(message + ": " + verify);
		} catch (Exception e) {
			message = messageSource.getMessage("Insert.Error.Verify", null, LocaleContextHolder.getLocale());
			log.error(message + ": " + verify);
			throw new RequestException(message);
		}
	}

	@Override
	public void delete(Long verifyId) {
		String message;
		try {
			verifyRepository.deleteById(verifyId);
			message = messageSource.getMessage("Delete.Success.Verify", null, LocaleContextHolder.getLocale());
			log.info(message + ": " + verifyId);
		} catch (Exception e) {
			message = messageSource.getMessage("Delete.Error.Verify", null, LocaleContextHolder.getLocale());
			log.error(message + ": " + verifyId);
			throw new RequestException(message);
		}
	}

	@Override
	public Verify findByLink(String link) {
		String message;
		Verify verify = null;
		
		verify = verifyRepository.findByOtp(link);
		if(verify != null) {
			message = messageSource.getMessage("Find.Success.Verify.Link", null, LocaleContextHolder.getLocale());
			log.info(message + ": " + link);
			return verifyRepository.findByOtp(link);
		} else {
			message = messageSource.getMessage("Find.Error.Verify.Link", null, LocaleContextHolder.getLocale());
			log.error(message + ": " + link);
			throw new RequestException(message);
		}
	}

	@Override
	public Verify findByOtp(String otp) {
		String message;
		Verify verify = null;
		
		verify = verifyRepository.findByOtp(otp);
		if(verify != null) {
			message = messageSource.getMessage("Find.Success.Verify.Otp", null, LocaleContextHolder.getLocale());
			log.info(message + ": " + otp);
			return verifyRepository.findByOtp(otp);
		} else {
			message = messageSource.getMessage("Find.Error.Verify.Otp", null, LocaleContextHolder.getLocale());
			log.error(message + ": " + otp);
			throw new RequestException(message);
		}
	}
}
