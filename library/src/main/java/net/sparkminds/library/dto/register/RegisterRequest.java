package net.sparkminds.library.dto.register;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.library.service.ValidPhoneNumber;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

	@Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", 
			message = "{registerrequest.email.email-invalid}")
	@Schema(description = "Email", example = "user@gmail.com")
	private String email;
	
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$", 
			message = "{registerrequest.password.password-invalid}")
	@Schema(description = "Password", example = "Abc1234!")
	private String password;
	
	@NotBlank(message = "{registerrequest.firstname.firstname-notblank}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{registerrequest.firstname.firstname-invalid}")
	@Length(max = 50, message = "{registerrequest.firstname.firstname-length}")
	@Schema(description = "Firstname", example = "H")
	private String firstname;
	
	@NotBlank(message = "{registerrequest.lastname.lastname-notblank}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{registerrequest.lastname.lastname-invalid}")
	@Length(max = 50, message = "{registerrequest.lastname.lastname-length}")
	@Schema(description = "Lastname", example = "Nguyễn Văn")
	private String lastname;
	
	@ValidPhoneNumber(countryCode = "VN", message = "{registerrequest.phone.phone-invalid}")
	@NotBlank(message = "{registerrequest.phone.phone-notblank}")
	@Schema(description = "Phone", example = "0378985473")
	private String phone;
	
	@NotBlank(message = "{registerrequest.address.address-notblank}")
	@Length(max = 255, message = "{registerrequest.address.address-length}")
	@Schema(description = "Address", example = "24A, Bàu Cát 2, Tân Bình, HCM")
	private String address;
	
	@NotBlank(message = "{registerrequest.avatar.avatar-notblank}")
	@Length(max = 50000, message = "{registerrequest.avatar.avatar-length}")
	@Schema(description = "Avatar", example = "default.png")
	private String avatar;
}
