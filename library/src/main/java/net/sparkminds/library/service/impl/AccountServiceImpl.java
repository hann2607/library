package net.sparkminds.library.service.impl;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.service.AccountService;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final MessageSource messageSource;

	@Override
	public List<Account> findByEmail(String email) {
		String message = null;
		
		List<Account> accounts = accountRepository.findByEmail(email);
		if(!accounts.isEmpty()) {
			message = messageSource.getMessage("account.email.find-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + email);
		}
		
		return accounts;
	}

	@Override
	public void create(Account account) {
		String message;
		
		try {
			accountRepository.save(account);
			message = messageSource.getMessage("account.insert-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + account.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("account.insert-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + account.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), 
					"account.insert-failed");
		}
	}
	
	@Override
	public void update(Account account) {
		String message;
		
		try {
			accountRepository.save(account);
			message = messageSource.getMessage("account.update-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + account.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("account.update-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + account.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), 
					"account.update-failed");
		}
	}
}
