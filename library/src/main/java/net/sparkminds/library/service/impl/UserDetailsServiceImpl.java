package net.sparkminds.library.service.impl;

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
    	Account account = null;
    	
        account = accountRepository.findByEmail(email).get(0);
        
        if(account == null){
        	message = messageSource.getMessage("Find.Error.Account.email", null,
					LocaleContextHolder.getLocale());
			log.error(message);
			throw new RequestException(message, HttpStatus.NOT_FOUND.value(),
					"Find.Error.Account.email");
        }
        
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(account.getRole().getRole().name()));

        return new User(account.getEmail(),account.getPassword(), authorities);
    }
}
