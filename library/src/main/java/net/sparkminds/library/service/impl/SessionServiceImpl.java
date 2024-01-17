package net.sparkminds.library.service.impl;

import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Session;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.SessionRepository;
import net.sparkminds.library.service.SessionService;

@Service
@RequiredArgsConstructor
@Log4j2
public class SessionServiceImpl implements SessionService {
	
	private final SessionRepository sessionRepository;
	private final MessageSource messageSource;
	
	@Override
	public void create(Session session) {
		String message = null;
		
		try {
			sessionRepository.save(session);
			message = messageSource.getMessage("session.insert-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + session.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("session.insert-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + session.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"session.insert-failed");
		}
	}

	@Override
	public void update(Session session) {
		String message = null;
		
		try {
			sessionRepository.save(session);
			message = messageSource.getMessage("session.update-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + session.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("session.update-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + session.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"session.update-failed");
		}
	}

	@Override
	public Optional<Session> findByJti(String JTI) {
		return sessionRepository.findByJti(JTI);
	}
}
