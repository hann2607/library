package net.sparkminds.library.jwt;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {
	
	@Value("${jwt.SecretKey}")
	private String secretKey;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractJTI(String token) {
		return extractClaim(token, Claims::getId);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public String generateToken(String userName, List<String> roles, String JTI) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);
		return createToken(claims, userName, JTI);
	}

	public String generateRefreshToken(String userName, List<String> roles, String JTI) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);
		return createRefreshToken(claims, userName, JTI);
	}

	private String createToken(Map<String, Object> claims, String userName, String JTI) {
		return Jwts.builder().setClaims(claims).setId(JTI).setSubject(userName).setIssuedAt(Date.from(Instant.now()))
				.setExpiration(Date.from(Instant.now().plusSeconds(4 * 60 * 60)))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	private String createRefreshToken(Map<String, Object> claims, String userName, String JTI) {
		return Jwts.builder().setClaims(claims).setId(JTI).setSubject(userName).setIssuedAt(Date.from(Instant.now()))
				.setExpiration(Date.from(Instant.now().plusSeconds(30 * 24 * 60 * 60)))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}