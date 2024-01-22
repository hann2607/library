package net.sparkminds.library.dto.changeinfo;

import java.io.Serializable;

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
	
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$", 
			message = "{account.password.password-invalid}")
			@Schema(description = "Password", example = "Abc1234!")
	private String password;
	
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$", 
			message = "{account.newpassword.newpassword-invalid}")
	@Schema(description = "New password", example = "Abc1234!")
	private String newPassword;
}
