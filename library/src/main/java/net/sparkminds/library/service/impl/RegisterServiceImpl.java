package net.sparkminds.library.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.UserDTO;
import net.sparkminds.library.entity.User;
import net.sparkminds.library.mapper.UserMapper;
import net.sparkminds.library.repository.UserRepository;
import net.sparkminds.library.service.RegisterService;

@Service
@RequiredArgsConstructor
@Log4j2
public class RegisterServiceImpl implements RegisterService{
	
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	
	@Override
	public void register(Optional<UserDTO> userDTO) {
		if (userDTO.isPresent()) {
			User user = userMapper.dtoToModel(userDTO.get());
			userRepository.save(user);
			log.info(user);
		} else {
			log.error("User is empty!");
		}
	}
}
