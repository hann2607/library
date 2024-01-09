package net.sparkminds.library.restcontroller.publics;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.User;
import net.sparkminds.library.service.UserService;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/user")
public class UserRestController {
	private final UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> findAllUser() {
		return ResponseEntity.ok(userService.findAll());
	}
}
