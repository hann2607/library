package net.sparkminds.library.service.impl;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.User;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.UserRepository;
import net.sparkminds.library.service.UserService;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final MessageSource messageSource;
	
	@Override
	public List<User> findAll() {
		String message = null;
		
		List<User> users = userRepository.findAll();
		if(!users.isEmpty()) {
			message = messageSource.getMessage("user.user-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"user.user-notfound");
		}
		return users;
	}

	@Override
	public void save(User user) {
		String message = null;
		
		try {
			userRepository.save(user);
			message = messageSource.getMessage("user.insert-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + user.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("user.insert-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + user.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"user.insert-failed");
		}
	}

	@Override
	public User findById(Long id) {
		String message = null;
		User user = null;
		user = userRepository.findById(id).get();
		
		if(user != null) {
			message = messageSource.getMessage("user.id.find-successed", 
				null, LocaleContextHolder.getLocale());
		
			log.info(message + ": " + user.toString());
		}
		
		return user;
	}

	@Override
	public User findByEmail(String email) {
		String message = null;
		User user = null;
		user = userRepository.findByEmail(email);
		
		if(user != null) {
			message = messageSource.getMessage("user.email.find-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + user.toString());
		}
		
		return user;
	}

}
