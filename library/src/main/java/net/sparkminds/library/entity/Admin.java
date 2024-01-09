package net.sparkminds.library.entity;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "admin")
public class Admin extends Account {

	@Column(name = "fullname", nullable = false, unique = false, length = 100)
	@NotBlank(message = "{NotBlank.Admin.fullname}")
	@Length(max = 100, message = "{Length.Admin.fullname}")
	private String fullname;
	
	@Column(name = "position", nullable = false, unique = false, length = 100)
	@NotBlank(message = "{NotBlank.Admin.position}")
	@Length(max = 100, message = "{Length.Admin.position}")
	private String position;
}
