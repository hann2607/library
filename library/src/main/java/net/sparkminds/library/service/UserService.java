package net.sparkminds.library.service;

import net.sparkminds.library.entity.User;

public interface UserService {
	User findByEmail(String email);
}
