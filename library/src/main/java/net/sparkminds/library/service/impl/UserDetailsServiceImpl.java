package net.sparkminds.library.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.AccountRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	String message = null;
    	Optional<Account> account = null;
    	LocalDateTime currentDateTime = null;
    	
    	account = accountRepository.findByEmailAndStatus(email, EnumStatus.ACTIVE);
		if(!account.isPresent()) {
			message = messageSource.getMessage("account.email.email-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + email);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"account.email.email-notfound");
		}
        
        if(account.get().getStatus().compareTo(EnumStatus.DELETED) == 0) {
        	message = messageSource.getMessage("account.account-deleted", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"account.account-deleted");
        } 
        
        if(account.get().getStatus().compareTo(EnumStatus.BLOCKED) == 0) {
        	
        	currentDateTime= LocalDateTime.now();
        	long seconds = Duration.between(currentDateTime, account.get().getBlockedAt()).getSeconds();

        	if(seconds < 0) {
        		account.get().setBlockedAt(null);
        		account.get().setReasonBlocked(null);
        		account.get().setStatus(EnumStatus.ACTIVE);
        		
        		accountRepository.save(account.get());
    			message = messageSource.getMessage("account.update-successed", 
    					null, LocaleContextHolder.getLocale());
    			log.info(message + ": " + account.toString());
        	} else {
        		message = messageSource.getMessage("account.account-blocked", null,
    					LocaleContextHolder.getLocale());
    			log.error(message);
    			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
    					"account.account-blocked");
        	}
        } 
        
        List<GrantedAuthority> authorities = 
        		List.of(new SimpleGrantedAuthority(account.get().getRole().getRole().name()));

        return new User(account.get().getEmail(),account.get().getPassword(), authorities);
    }
}
