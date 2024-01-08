package net.sparkminds.library.service;

import java.util.Optional;

import net.sparkminds.library.dto.UserDTO;

public interface RegisterService {
	void register(Optional<UserDTO> userDTO);
}
