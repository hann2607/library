package net.sparkminds.library.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
	@Id
	private Long id;
	private String email;
	private String password;
	private LocalDateTime blockedAt;
	private String reasonBlocked;
	private String firstname;
	private String middlename;
	private String lastname;
	private String phone;
	private String address;
	private String status;
}
