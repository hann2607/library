package net.sparkminds.library.restcontroller.privates;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.repository.CustomerRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin APIs")
public class AdminRestController {
	private final CustomerRepository customerRepository;
	
	@GetMapping("/users")
	public ResponseEntity<List<Customer>> findAllUser() {
		return ResponseEntity.ok(customerRepository.findAll());
	}
}
