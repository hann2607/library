package net.sparkminds.library.service;

import java.util.List;

import net.sparkminds.library.entity.Account;

public interface AccountService {
	List<Account> findByEmail(String email);
}
