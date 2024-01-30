package net.sparkminds.library.dto.membermanagement;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.library.service.validator.ValidPhoneNumber;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberManaRequest implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name = "email", nullable = false, unique = true, length = 100)
	@Email(message = "{MemberManaRequest.email.email-invalid}")
	@Length(max = 100, message = "{MemberManaRequest.email.email-invalidlength}")
	@NotNull(message = "{MemberManaRequest.email.email-invalid}")
	private String email;

	@Column(name = "password", nullable = false, length = 60)
	@Length(min = 8, max = 60, message = "{MemberManaRequest.password.password-invalidlength}")
	@NotNull(message = "{MemberManaRequest.email.email-invalid}")
	private String password;
	
	@Column(name = "firstname", nullable = false, unique = false, length = 50)
	@NotBlank(message = "{MemberManaRequest.firstname.firstname-notblank}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{MemberManaRequest.firstname.firstname-invalid}")
	@Length(max = 50, message = "{MemberManaRequest.firstname.firstname-length}")
	private String firstname;

	@Column(name = "lastname", nullable = false, unique = false, length = 50)
	@NotBlank(message = "{MemberManaRequest.lastname.lastname-notblank}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{MemberManaRequest.lastname.lastname-invalid}")
	@Length(max = 50, message = "{MemberManaRequest.lastname.lastname-length}")
	private String lastname;

	@Column(name = "phone", nullable = false, unique = true, length = 10)
	@ValidPhoneNumber(countryCode = "VN", message = "{MemberManaRequest.phone.phone-invalid}")
	@NotBlank(message = "{MemberManaRequest.phone.phone-notblank}")
	private String phone;

	@Column(name = "address", nullable = false, unique = false, length = 255)
	@NotBlank(message = "{MemberManaRequest.address.address-notblank}")
	@Length(max = 255, message = "{MemberManaRequest.address.address-length}")
	private String address;
}
