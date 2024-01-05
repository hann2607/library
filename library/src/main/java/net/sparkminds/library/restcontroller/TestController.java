package net.sparkminds.library.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.service.AccountService;

@RestController
@Log4j2
public class TestController {

	@Autowired
	AccountService accountService;
	
	@GetMapping("/api/user-management/users/user")
	public List<Account> testAPI() {
		
		return accountService.getAll();
	}
}
