package net.sparkminds.library.service;

import net.sparkminds.library.dto.register.RegisterRequest;

public interface RegisterService {
	RegisterRequest register(RegisterRequest userDTO);
}
