package net.sparkminds.library.dto.register;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
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
	@Email(message = "{registerrequest.email.email-invalid}")
	@Length(max = 100, message = "{registerrequest.email.email-length}")
	private String email;
	
	@Length(min = 8, max = 32, message = "{registerrequest.password.password-invalid}")
	private String password;
	
	@NotBlank(message = "{registerrequest.firstname.firstname-notblank}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{registerrequest.firstname.firstname-invalid}")
	@Length(max = 50, message = "{registerrequest.firstname.firstname-length}")
	private String firstname;
	
	@NotBlank(message = "{registerrequest.lastname.lastname-notblank}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{registerrequest.lastname.lastname-invalid}")
	@Length(max = 50, message = "{registerrequest.lastname.lastname-length}")
	private String lastname;
	
	@ValidPhoneNumber(countryCode = "VN", message = "{registerrequest.phone.phone-invalid}")
	@NotBlank(message = "{registerrequest.phone.phone-notblank}")
	private String phone;
	
	@NotBlank(message = "{registerrequest.address.address-notblank}")
	@Length(max = 255, message = "{registerrequest.address.address-length}")
	private String address;
	
	private String status;
	
	@NotBlank(message = "{registerrequest.avatar.avatar-notblank}")
	@Length(max = 255, message = "{registerrequest.avatar.avatar-length}")
	private String avatar;
}
