package net.sparkminds.library.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.User;
import net.sparkminds.library.repository.UserRepository;
import net.sparkminds.library.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	
	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

}
