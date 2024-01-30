package net.sparkminds.library.restcontroller.privates;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.membermanagement.MemberManaRequest;
import net.sparkminds.library.dto.membermanagement.MemberManaResponse;
import net.sparkminds.library.service.MemberManaService;
import net.sparkminds.library.service.criteria.CustomerCriteria;
import net.sparkminds.library.service.query.CustomerQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Tag(name = "Member management", description = "Member management APIs")
public class MemberManaRestController {
	private final CustomerQueryService customerQueryService;
	private final MemberManaService MemberManaService;
	
	@Operation(summary = "Search member", 
			description = "The response is list member.", 
			tags = { "Member management", "get" })
	@GetMapping("/members")
	public ResponseEntity<Page<MemberManaResponse>> searchMember(CustomerCriteria customerCriteria, Pageable pageable) {
		return ResponseEntity.ok(customerQueryService.findMemberByCriteria(customerCriteria, pageable));
	}
	
	@Operation(summary = "Create new Member", 
			description = "The response is MemberManaRequest.", 
			tags = { "Member management", "post" })
	@PostMapping(value = "/members", consumes = { "multipart/form-data" })
	public ResponseEntity<Void> createMember(@Valid @ModelAttribute MemberManaRequest MemberManaRequest) {
		MemberManaService.create(MemberManaRequest);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Update Member", 
			description = "The response is MemberManaRequest.", 
			tags = { "Member management", "put" })
	@PutMapping(value = "/members", consumes = { "multipart/form-data" })
	public ResponseEntity<Void> updateMember(@Valid @ModelAttribute MemberManaRequest MemberManaRequest) {
		MemberManaService.update(MemberManaRequest);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Delete Member", 
			description = "Delete Member by id.", 
			tags = { "Member management", "delete" })
	@DeleteMapping("/members/{id}")
	public ResponseEntity<Void> updateMember(@PathVariable(name = "id") Long id) {
		MemberManaService.delete(id);
		return ResponseEntity.ok().build();
	}
}
