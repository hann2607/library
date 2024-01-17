package net.sparkminds.library.service;

import net.sparkminds.library.jwt.JwtRequest;
import net.sparkminds.library.jwt.JwtResponse;

public interface AuthenticationService {
	JwtResponse authentication(JwtRequest jwtRequest);
}
