package net.sparkminds.library.dto.mfa;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaRequest {
	
	@Email(message = "{account.email.email-invalid}")
	@Length(max = 100, message = "{account.email.email-invalidlength}")
	private String email;
}
