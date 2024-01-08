package net.sparkminds.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "user")
public class User extends Account {

	@Column(name = "firstname", nullable = false, unique = false, length = 50)
	@NotNull
	private String firstname;

	@Column(name = "middlename", nullable = false, unique = false, length = 50)
	@NotNull
	private String middlename;

	@Column(name = "lastname", nullable = false, unique = false, length = 50)
	@NotNull
	private String lastname;

	@Column(name = "phone", nullable = false, unique = true, length = 10)
	@NotNull
	private String phone;

	@Column(name = "address", nullable = false, unique = false, length = 255)
	@NotNull
	private String address;
}
