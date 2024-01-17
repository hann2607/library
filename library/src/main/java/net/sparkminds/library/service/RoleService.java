package net.sparkminds.library.service;

import net.sparkminds.library.entity.Role;
import net.sparkminds.library.enumration.EnumRole;

public interface RoleService {
	Role findByRole(EnumRole roleName);
}
