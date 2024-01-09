package net.sparkminds.library.entity;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.sparkminds.library.service.ValidPhoneNumber;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "user")
public class User extends Account {

	@Column(name = "firstname", nullable = false, unique = false, length = 50)
	@NotBlank(message = "{NotBlank.User.firstName}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{Pattern.User.firstName}")
	@Length(max = 50, message = "{Length.User.firstname}")
	private String firstname;

	@Column(name = "lastname", nullable = false, unique = false, length = 50)
	@NotBlank(message = "{NotBlank.User.lastName}")
	@Pattern(regexp = "^[\\p{L} ]+$", message = "{Pattern.User.lastName}")
	@Length(max = 50, message = "{Length.User.lastname}")
	private String lastname;

	@Column(name = "phone", nullable = false, unique = true, length = 10)
	@ValidPhoneNumber(countryCode = "VN", message = "{phone.invalid}")
	@NotBlank(message = "{NotBlank.User.phone}")
	private String phone;

	@Column(name = "address", nullable = false, unique = false, length = 255)
	@NotBlank(message = "{NotBlank.User.address}")
	@Length(max = 255, message = "{Length.User.address}")
	private String address;
	
	@Column(name = "avatar", length = 255)
	@NotBlank(message = "{NotBlank.User.avatar}")
	@Length(max = 255, message = "{Length.User.avatar}")
	private String avatar;
}
