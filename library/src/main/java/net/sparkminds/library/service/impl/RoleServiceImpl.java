package net.sparkminds.library.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.Role;
import net.sparkminds.library.repository.RoleRepository;
import net.sparkminds.library.service.RoleService;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
	
	private final RoleRepository roleRepository;
	
	@Override
	public Role findByRole(String RoleName) {
		return roleRepository.findByRole(RoleName);
	}

}
