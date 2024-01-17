package net.sparkminds.library.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Role;
import net.sparkminds.library.enumration.EnumRole;
import net.sparkminds.library.repository.RoleRepository;
import net.sparkminds.library.service.RoleService;

@Service
@RequiredArgsConstructor
@Log4j2
public class RoleServiceImpl implements RoleService{
	
	private final RoleRepository roleRepository;
	private final MessageSource messageSource;

	@Override
	public Role findByRole(EnumRole roleName) {
		Role role = null;
		String message = null;
		
		role = roleRepository.findByRole(roleName).orElse(new Role());
		
		if(role == null) {
			message = messageSource.getMessage("role.role.find-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + roleName);
		}
		
		return role;
	}
}
