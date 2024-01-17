package net.sparkminds.library.dto.jwt;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Email(message = "{account.email.email-invalid}")
	@Length(max = 100, message = "{account.email.email-invalidlength}")
	private String username;
	
	@Length(min = 8, max = 60, message = "{account.password.password-invalidlength}")
	private String password;
}
