package net.sparkminds.library.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import net.sparkminds.library.enumration.EnumTypeOTP;

@Entity
@Table(name = "verify")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Verify{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "link", nullable = true, unique = true, length = 255)
	private String link;
	
	@Column(name = "otp", nullable = true, unique = true, length = 6)
	private String otp;

	@Column(name = "expiration_time", nullable = false)
	private LocalDateTime expirationTime;

	@Column(name = "type_otp")
	@Enumerated(EnumType.STRING)
	private EnumTypeOTP typeOTP;
	
	@ManyToOne(optional=false)
    @JoinColumn(name="account_id", nullable=false)
    private Account account;

	@Override
	public String toString() {
		return "Verify [id=" + id + ", link=" + link + ", otp=" + otp + ", expirationTime=" + expirationTime
				+ ", typeOTP=" + typeOTP + "]";
	}
}
