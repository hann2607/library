package net.sparkminds.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")
public class Admin extends Account {

	@Column(name = "fullname", nullable = false, unique = false, length = 100)
	private String fullname;

	@Column(name = "position", nullable = false, unique = false, length = 100)
	private String position;
}
