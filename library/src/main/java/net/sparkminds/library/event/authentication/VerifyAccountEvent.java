package net.sparkminds.library.event.authentication;

import org.springframework.context.ApplicationEvent;

import net.sparkminds.library.dto.jwt.JwtRequest;

public class VerifyAccountEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	
	private JwtRequest jwtRequest;

	public VerifyAccountEvent(Object source, JwtRequest jwtRequest) {
		super(source);
		this.jwtRequest = jwtRequest;
	}

	public JwtRequest getJwtRequest() {
		return jwtRequest;
	}
}
