package net.sparkminds.library.restcontroller.common;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.UserDTO;
import net.sparkminds.library.entity.User;
import net.sparkminds.library.mapper.UserMapper;
import net.sparkminds.library.repository.RoleRepository;
import net.sparkminds.library.service.RegisterService;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/common")
public class RegisterController {

	private final RegisterService registerService;

	@PostMapping("/register")
	public ResponseEntity<UserDTO> registerAccount(@RequestBody Optional<UserDTO> userDTO) {
		registerService.register(userDTO);
		return ResponseEntity.ok(userDTO.get());
	}
}
