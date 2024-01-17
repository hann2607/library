package net.sparkminds.library.jwt;

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
	
	@Email(message = "{Email.Account.email}")
	@Length(max = 100, message = "{Length.Account.email}")
	private String username;
	
	@Length(min = 8, max = 60, message = "{Length.Account.Password}")
	private String password;
}
