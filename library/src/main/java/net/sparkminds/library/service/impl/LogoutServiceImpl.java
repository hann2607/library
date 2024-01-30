package net.sparkminds.library.service.impl;

import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Session;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.jwt.JwtUtil;
import net.sparkminds.library.repository.SessionRepository;
import net.sparkminds.library.service.LogoutService;

@Service
@RequiredArgsConstructor
@Log4j2
public class LogoutServiceImpl implements LogoutService {
	
	private final MessageSource messageSource;
	private final SessionRepository sessionRepository;
	private final JwtUtil jwtUtil;
	
	@Override
	public void logout() {
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
        
        session.get().setLogin(false);
        
        sessionRepository.save(session.get());
		message = messageSource.getMessage("session.update-successed", 
				null, LocaleContextHolder.getLocale());
		log.info(message + ": " + session.toString());
	}

}
