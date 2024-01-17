package net.sparkminds.library.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
import net.sparkminds.library.service.AccountService;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {

	private final AccountService accountService;
	private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	String message = null;
    	Account account = null;
    	LocalDateTime currentDateTime = null;
    	
    	account = accountService.findByEmail(email).get(0);
        
        if(account == null){
        	message = messageSource.getMessage("account.email.email-notfound", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"account.email.email-notfound");
        }
        
        if(account.getStatus().compareTo(EnumStatus.DELETED) == 0) {
        	message = messageSource.getMessage("account.account-deleted", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"account.account-deleted");
        } 
        
        if(account.getStatus().compareTo(EnumStatus.BLOCKED) == 0) {
        	
        	currentDateTime= LocalDateTime.now();
        	long seconds = Duration.between(currentDateTime, account.getBlockedAt()).getSeconds();

        	if(seconds < 0) {
        		account.setBlockedAt(null);
        		account.setReasonBlocked(null);
        		account.setStatus(EnumStatus.ACTIVE);
        		
        		accountService.update(account);
        	} else {
        		message = messageSource.getMessage("account.account-blocked", null,
    					LocaleContextHolder.getLocale());
    			log.error(message);
    			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
    					"account.account-blocked");
        	}
        } 
        
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(account.getRole().getRole().name()));

        return new User(account.getEmail(),account.getPassword(), authorities);
    }
}
