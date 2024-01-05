package net.sparkminds.library.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sparkminds.library.entity.Account;
import net.sparkminds.library.repository.AccountRepository;
import net.sparkminds.library.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService{
	
	@Autowired
	AccountRepository accountRepository;

	@Override
	public List<Account> getAll() {
		return accountRepository.findAll();
	}
}
