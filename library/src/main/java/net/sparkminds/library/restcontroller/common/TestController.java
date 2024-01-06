package net.sparkminds.library.restcontroller.common;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.service.AccountService;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/user-management")
@Log4j2
public class TestController {
	private final AccountService accountService;
	
	@GetMapping("users")
	public List<Account> getAll() {
		return accountService.getAll();
	}
}
