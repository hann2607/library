package net.sparkminds.library.dto.register;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.library.entity.Role;
import net.sparkminds.library.service.ValidPhoneNumber;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {
	@Email(message = "{Email.RegisterRequest.email}")
	@Length(max = 100, message = "{Length.RegisterRequest.email}")
	private String email;
	
	@Length(min = 8, max = 32, message = "{Length.RegisterRequest.Password}")
	private String password;
	
	@NotBlank(message = "{NotBlank.RegisterRequest.firstName}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{Pattern.RegisterRequest.firstName}")
	@Length(max = 50, message = "{Length.RegisterRequest.firstname}")
	private String firstname;
	
	@NotBlank(message = "{NotBlank.RegisterRequest.lastName}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{Pattern.RegisterRequest.lastName}")
	@Length(max = 50, message = "{Length.RegisterRequest.lastname}")
	private String lastname;
	
	@ValidPhoneNumber(countryCode = "VN", message = "{phone.invalid}")
	@NotBlank(message = "{NotBlank.RegisterRequest.phone}")
	private String phone;
	
	@NotBlank(message = "{NotBlank.RegisterRequest.address}")
	@Length(max = 255, message = "{Length.RegisterRequest.address}")
	private String address;
	
	private String status;
	
	@NotBlank(message = "{NotBlank.RegisterRequest.avatar}")
	@Length(max = 255, message = "{Length.RegisterRequest.avatar}")
	private String avatar;
	
	@NotNull(message = "{NotNull.RegisterRequest.role}")
	private Role role;
}
