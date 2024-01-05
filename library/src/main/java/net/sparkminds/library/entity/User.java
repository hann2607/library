package net.sparkminds.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User extends Account {

	@Column(name = "firstname", nullable = false, unique = false, length = 50)
	private String firstname;

	@Column(name = "middlename", nullable = false, unique = false, length = 50)
	private String middlename;

	@Column(name = "lastname", nullable = false, unique = false, length = 50)
	private String lastname;

	@Column(name = "phone", nullable = false, unique = true, length = 10)
	private String phone;

	@Column(name = "address", nullable = false, unique = false, length = 255)
	private String address;
}
