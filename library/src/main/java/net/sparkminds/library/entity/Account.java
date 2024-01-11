package net.sparkminds.library.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.sparkminds.library.enumration.EnumStatus;

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
	@Email(message = "{account.email.email-invalid}")
	@Length(max = 100, message = "{account.email.email-invalidlength}")
	private String email;

	@Column(name = "password", nullable = false, length = 60)
	@Length(min = 8, max = 60, message = "{account.password.password-invalidlength}")
	private String password;

	@Column(name = "blocked_at", nullable = true)
	private LocalDateTime blockedAt;

	@Column(name = "reason_blocked", nullable = true, length = 255)
	private String reasonBlocked;

	@Column(name = "isVerify", nullable = false)
	@NotNull(message = "{account.isverify.isverify-notnull}")
	private boolean isVerify;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private EnumStatus status;

	@JsonIgnore
	@OneToMany(mappedBy = "account")
	private List<Session> sessions;

	@ManyToOne(optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Override
	public String toString() {
		return "Account [id=" + id + ", email=" + email + ", password=" + password + ", blockedAt=" + blockedAt
				+ ", reasonBlocked=" + reasonBlocked + ", isVerify=" + isVerify + ", status=" + status + "]";
	}
}
