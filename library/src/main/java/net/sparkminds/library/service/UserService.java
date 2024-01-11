package net.sparkminds.library.service;

import java.util.List;

import net.sparkminds.library.entity.User;

public interface UserService {
	List<User> findAll();
	
	User findById(Long id);
	
	void save(User user);

	User findByEmail(String email);
}
