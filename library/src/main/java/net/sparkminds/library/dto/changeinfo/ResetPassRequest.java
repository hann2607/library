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
public class ResetPassRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", 
			message = "{account.email.email-invalid}")
	@Schema(description = "Email", example = "user@gmail.com")
	private String username;
}
