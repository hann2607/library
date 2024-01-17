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
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.service.CustomerService;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {

	private final CustomerService customerService;
	private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	String message = null;
    	Customer customer = null;
    	LocalDateTime currentDateTime = null;
    	
    	customer = customerService.findByEmail(email);
        
        if(customer == null){
        	message = messageSource.getMessage("account.email.email-notfound", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"account.email.email-notfound");
        }
        
        if(customer.getStatus().compareTo(EnumStatus.DELETED) == 0) {
        	message = messageSource.getMessage("account.account-deleted", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"account.account-deleted");
        } 
        
        if(customer.getStatus().compareTo(EnumStatus.BLOCKED) == 0) {
        	
        	currentDateTime= LocalDateTime.now();
        	long seconds = Duration.between(currentDateTime, customer.getBlockedAt()).getSeconds();

        	if(seconds < 0) {
        		customer.setBlockedAt(null);
        		customer.setReasonBlocked(null);
        		customer.setStatus(EnumStatus.ACTIVE);
        		
        		customerService.update(customer);
        	} else {
        		message = messageSource.getMessage("account.account-blocked", null,
    					LocaleContextHolder.getLocale());
    			log.error(message);
    			throw new RequestException(message, HttpStatus.UNAUTHORIZED.value(),
    					"account.account-blocked");
        	}
        } 
        
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(customer.getRole().getRole().name()));

        return new User(customer.getEmail(),customer.getPassword(), authorities);
    }
}
