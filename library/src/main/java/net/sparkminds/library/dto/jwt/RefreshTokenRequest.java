package net.sparkminds.library.dto.jwt;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = "{RefreshTokenRequest.refreshToken.refreshToken-notblank}")
	@Length(max = 255, message = "{RefreshTokenRequest.refreshToken.refreshToken-invalidlength}")
	private String refreshToken;
}
