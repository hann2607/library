package net.sparkminds.library.entity;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.sparkminds.library.service.ValidPhoneNumber;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Table(name = "user")
public class User extends Account {

	@Column(name = "firstname", nullable = false, unique = false, length = 50)
	@NotBlank(message = "{user.firstname.firstname-notblank}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{user.firstname.firstname-invalid}")
	@Length(max = 50, message = "{user.firstname.firstname-length}")
	private String firstname;

	@Column(name = "lastname", nullable = false, unique = false, length = 50)
	@NotBlank(message = "{user.lastname.lastname-notblank}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{user.lastname.lastname-invalid}")
	@Length(max = 50, message = "{user.lastname.lastname-length}")
	private String lastname;

	@Column(name = "phone", nullable = false, unique = true, length = 10)
	@ValidPhoneNumber(countryCode = "VN", message = "{user.phone.phone-invalid}")
	@NotBlank(message = "{user.phone.phone-notblank}")
	private String phone;

	@Column(name = "address", nullable = false, unique = false, length = 255)
	@NotBlank(message = "{user.address.address-notblank}")
	@Length(max = 255, message = "{user.address.address-length}")
	private String address;
	
	@Column(name = "avatar", length = 255)
	@NotBlank(message = "{user.avatar.avatar-notblank}")
	@Length(max = 255, message = "{user.avatar.avatar-length}")
	private String avatar;
}
