package net.sparkminds.library.dto.changeinfo;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", 
			message = "{account.email.email-invalid}")
	@Schema(description = "Email", example = "user@gmail.com")
	private String username;
	
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$", 
			message = "{account.password.password-invalid}")
			@Schema(description = "Password", example = "Abc1234!")
	private String password;
	
	@Length(min = 8, max = 60, message = "{account.newpassword.newpassword-invalidlength}")
	@Schema(description = "New password", example = "12345678")
	private String newPassword;
}
