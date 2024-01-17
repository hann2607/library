package net.sparkminds.library.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.UserRepository;
import net.sparkminds.library.service.CustomerService;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CustomerServiceImpl implements CustomerService {
	
	private final UserRepository userRepository;
	private final MessageSource messageSource;
	
	@Override
	public List<Customer> findAll() {
		String message = null;
		
		List<Customer> users = userRepository.findAll();
		if(!users.isEmpty()) {
			message = messageSource.getMessage("user.findall-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message);
		}
		return users;
	}

	@Override
	public void create(Customer user) {
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
	public void update(Customer user) {
		String message = null;
		
		try {
			userRepository.save(user);
			message = messageSource.getMessage("user.update-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + user.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("user.update-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + user.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"user.update-failed");
		}
	}

	@Override
	public Customer findById(Long id) {
		String message = null;
		Optional<Customer> user = null;
		user = userRepository.findById(id);
		
		if(user.isPresent()) {
			message = messageSource.getMessage("user.id.find-successed", 
				null, LocaleContextHolder.getLocale());
		
			log.info(message + ": " + user.toString());
			return user.get();
		}
		return null;
	}

	@Override
	public Customer findByEmail(String email) {
		String message = null;
		Optional<Customer> user = null;
		user = userRepository.findByEmail(email);
		
		if(user.isPresent()) {
			message = messageSource.getMessage("user.email.find-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + user.toString());
			return user.get();
		}
		return null;
	}

}
