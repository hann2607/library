package net.sparkminds.library.restcontroller.privates;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.service.criteria.CustomerCriteria;
import net.sparkminds.library.service.query.CustomerQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Tag(name = "Member management", description = "Member management APIs")
public class MemberRestController {
	private final CustomerQueryService customerQueryService;
	
	@Operation(summary = "Get all users", 
			description = "The response is list users.", 
			tags = { "Member management", "get" })
	@GetMapping("/users")
	public ResponseEntity<List<Customer>> findAllUser(CustomerCriteria customerCriteria) {
		return ResponseEntity.ok(customerQueryService.findByCriteria(customerCriteria));
	}
}
