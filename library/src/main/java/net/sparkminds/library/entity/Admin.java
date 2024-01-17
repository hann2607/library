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
	@NotBlank(message = "{admin.fullname.fullname-notblank}")
	@Length(max = 100, message = "{admin.fullname.fullname-invalidlength}")
	private String fullname;
	
	@Column(name = "position", nullable = false, unique = false, length = 100)
	@NotBlank(message = "{admin.position.position-notblank}")
	@Length(max = 100, message = "{admin.position.position-invalidlength}")
	private String position;

	@Override
	public String toString() {
		return "Admin [fullname=" + fullname + ", position=" + position + "]";
	}
}
