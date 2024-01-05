package net.sparkminds.library.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

@Entity
@Table(name = "session")
@Builder
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "session_data", nullable = false, unique = true, length = 255)
	private String sessionData;

	@Column(name = "expiration_time", nullable = false)
	private LocalDateTime expirationTime;

	@Column(name = "user_information", nullable = false, unique = true, length = 255)
	private String userInfo;
}
