package net.sparkminds.library.event;

import org.springframework.context.ApplicationEvent;

import net.sparkminds.library.dto.jwt.JwtResponse;

public class AuthenticationSuccessEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	
	private final JwtResponse jwtResponse;

	public AuthenticationSuccessEvent(Object source, JwtResponse jwtResponse) {
		super(source);
		this.jwtResponse = jwtResponse;
	}

	public JwtResponse getJwtResponse() {
		return jwtResponse;
	}
}
