package net.sparkminds.library.aspect;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Session;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.jwt.JwtUtil;
import net.sparkminds.library.repository.SessionRepository;

@Aspect
@Component
@RequiredArgsConstructor
@Log4j2
public class JwtTokenAspect {
	
	private final SessionRepository sessionRepository;
	private final MessageSource messageSource;
	private final JwtUtil jwtUtil;
	
	@Before("execution(* net.sparkminds.library.restcontroller..*(..))")
    public void checkAuthorization() {
		String message = null;
		String authHeader = null;
		String token = null;
		String JTI = null;
		HttpServletRequest request = null;
		Optional<Session> session = null;
		long seconds = 0;
		
		request = ((ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes()).getRequest();
		
        authHeader = request.getHeader("Authorization");
        if(authHeader != null) {
        	token = authHeader.substring(7);
        	JTI = jwtUtil.extractJTI(token);
            
            session = sessionRepository.findByJti(JTI);
            if(session.isPresent()) {
            	
            	// Check if it's refreshToken?
    			seconds = Duration.between(session.get().getRefreshExpirationTime(),
    					convertDateToLocalDateTime(jwtUtil.extractExpiration(token))).getSeconds();
    			if (seconds == 0) {
    				message = messageSource.getMessage("token.token-invalid", null,
    						LocaleContextHolder.getLocale());
    				log.error(message);
    				throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), "token.token-invalid");
    			}
            	
            	if(!session.get().isLogin()) {
            		message = messageSource.getMessage("account.account-logout", 
        					null, LocaleContextHolder.getLocale());
        			
        			log.error(message);
        			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(), 
        					"account.account-logout");
            	}
            }
        }  
    }
	
	private static LocalDateTime convertDateToLocalDateTime(Date date) {
		Instant instant = date.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		return LocalDateTime.ofInstant(instant, zoneId);
	}
}
