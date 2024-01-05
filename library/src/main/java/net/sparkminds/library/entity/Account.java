package net.sparkminds.library.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "password", nullable = false, length = 60)
	private BigInteger password;

	@Column(name = "blocked_at", nullable = true)
	private LocalDateTime blockedAt;

	@Column(name = "reason_blocked", nullable = true, length = 255)
	private String reasonBlocked;

	@Column(name = "status", nullable = false)
	private String status;

	@OneToOne
	@JoinColumn(name = "role_id", referencedColumnName = "Id", insertable = false, updatable = false)
	private Role role;
}
