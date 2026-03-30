package com.project.security.jwt;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService { // guarda los datos como email, id usuario y el rol para no crear a cada rato

	private final SecretKey key;
	private final long expirationMs;

	public JwtService(
			@Value("${app.security.jwt.secret}") String secret,
			@Value("${app.security.jwt.expiration-ms}") long expirationMs) {

		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	public String generateToken(String email, Long uid, String role) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMs);

		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(now)
				.setExpiration(exp)
				.addClaims(Map.of("uid", uid, "role", role))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	public boolean isTokenValid(String token) {
		try {
			extractAllClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException ex) {
			return false;
		}
	}
}
