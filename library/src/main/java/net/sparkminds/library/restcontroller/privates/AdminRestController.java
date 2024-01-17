package net.sparkminds.library.restcontroller.privates;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.service.CustomerService;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/admin")
public class AdminRestController {
	private final CustomerService userService;
	
	@GetMapping("/users")
	public ResponseEntity<List<Customer>> findAllUser() {
		return ResponseEntity.ok(userService.findAll());
	}
}
