package net.sparkminds.library.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "session")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Session{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "jti", nullable = false, unique = true, length = 255)
	@NotBlank(message = "{session.jti.jti-notblank}")
	private String jti;
	
	@Column(name = "isLogin", nullable = false)
	@NotNull(message = "{session.isLogin.isLogin-notnull}")
	private boolean isLogin;

	@Column(name = "refresh_expiration_time", nullable = false)
	@NotNull(message = "{session.expiration.expiration-notnull}")
	private LocalDateTime refreshExpirationTime;
	
	@ManyToOne(optional=false)
    @JoinColumn(name="account_id", nullable=false)
    private Account account;

	@Override
	public String toString() {
		return "Session [id=" + id + ", refreshToken=" + jti + ", refreshExpirationTime="
				+ refreshExpirationTime + "]";
	}
}
