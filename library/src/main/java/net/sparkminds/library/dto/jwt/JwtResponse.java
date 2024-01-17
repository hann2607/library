package net.sparkminds.library.dto.jwt;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JwtResponse implements Serializable  {
	private static final long serialVersionUID = 1L;
	private String token;
	private String refreshToken;
	private String type = "Bearer";
	private String username;
	private List<String> roles;

	public JwtResponse(String token, String username, List<String> roles, String refreshToken) {
		this.token = token;
		this.username = username;
		this.roles = roles;
		this.refreshToken = refreshToken;
	}
}
