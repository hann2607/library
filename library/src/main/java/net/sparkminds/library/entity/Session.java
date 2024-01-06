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
	
	@ManyToOne(optional=false)
    @JoinColumn(name="account_id", nullable=false)
    private Account account;
}
