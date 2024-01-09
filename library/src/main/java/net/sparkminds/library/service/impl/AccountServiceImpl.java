package net.sparkminds.library.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.service.AccountService;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	@Override
	public List<Account> findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}
}
