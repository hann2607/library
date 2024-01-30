package net.sparkminds.library.mapper.impl;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.membermanagement.MemberManaRequest;
import net.sparkminds.library.dto.membermanagement.MemberManaResponse;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.entity.Role;
import net.sparkminds.library.enumration.EnumRole;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.mapper.MemberManaMapper;
import net.sparkminds.library.repository.RoleRepository;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-01-29T17:22:38+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 1.4.300.v20221108-0856, environment: Java 18.0.2.1 (Oracle Corporation)"
)

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberManaMapperImpl implements MemberManaMapper {

	private final RoleRepository roleRepository;
	private final MessageSource messageSource;
	
    @Override
    public MemberManaResponse modelToDto(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        MemberManaResponse memberManaResponse = new MemberManaResponse();

        BeanUtils.copyProperties(customer, memberManaResponse);
        memberManaResponse.setRoleName(customer.getRole().getRole());

        return memberManaResponse;
    }

    @Override
    public Customer dtoToModel(MemberManaRequest memberManaRequest) {
    	Optional<Role> role = null;
    	String message = null;
    	
        if ( memberManaRequest == null ) {
            return null;
        }

        role = roleRepository.findByRole(EnumRole.ROLE_USER);
        if(role.isPresent()) {
        	message = messageSource.getMessage("role.role-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + role.get().toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"role.role-notfound");
        }
        
        
        Customer customer = new Customer();

        BeanUtils.copyProperties(memberManaRequest, customer);
        customer.setAvatar(null);
        customer.setLoginAttempt(0);
        customer.setFirstTimeLogin(false);
        customer.setVerify(false);
        customer.setMfa(false);
        customer.setStatus(EnumStatus.ACTIVE);
        customer.setRole(role.get());

        return customer;
    }
}
