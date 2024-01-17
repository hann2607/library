package net.sparkminds.library.service;

import net.sparkminds.library.dto.jwt.JwtRequest;
import net.sparkminds.library.dto.jwt.JwtResponse;
import net.sparkminds.library.dto.jwt.RefreshTokenRequest;

public interface AuthenticationService {
	JwtResponse authentication(JwtRequest jwtRequest);
	
	JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
