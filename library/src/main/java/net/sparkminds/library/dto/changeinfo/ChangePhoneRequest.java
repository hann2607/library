package net.sparkminds.library.dto.changeinfo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.library.service.ValidPhoneNumber;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePhoneRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@ValidPhoneNumber(countryCode = "VN", message = "{account.newphone.newphone-invalid}")
	@NotBlank(message = "{account.newphone.newphone-notblank}")
	@Schema(description = "new phone number", example = "0378985473")
	private String newPhone;
}
