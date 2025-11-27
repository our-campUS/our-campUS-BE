package com.campus.campus.global.util.jwt;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.campus.campus.global.util.jwt.exception.InvalidJwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {

	private final JwtAuthenticator jwtAuthenticator;

	@Value("${jwt.access.expiration-seconds}")
	private long accessTokenExpirationSeconds;

	@Value("${jwt.refresh.expiration-seconds}")
	private long refreshTokenExpirationSeconds;

	private SecretKey accessKey;
	private SecretKey refreshKey;

	@PostConstruct
	void init() {
		// JwtAuthenticator 내부 key를 그대로 써도 되지만, 여기서는 getter 사용
		this.accessKey = jwtAuthenticator.getAccessKey();
		this.refreshKey = jwtAuthenticator.getRefreshKey();
	}

	public String createAccessToken(Long userId) {
		Instant now = Instant.now();
		return Jwts.builder()
			.subject(String.valueOf(userId))
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusSeconds(accessTokenExpirationSeconds)))
			.claim("role", "USER")
			.signWith(accessKey)
			.compact();
	}

	public String createRefreshToken(Long userId) {
		Instant now = Instant.now();
		return Jwts.builder()
			.subject(String.valueOf(userId))
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusSeconds(refreshTokenExpirationSeconds)))
			.claim("role", "USER")
			.signWith(refreshKey)
			.compact();
	}

	public Long getUserIdFromAccessToken(String token) {
		Claims claims = jwtAuthenticator.parseAccessToken(token).getPayload();
		String role = claims.get("role", String.class);
		if (!"USER".equals(role)) {
			throw new InvalidJwtException();
		}

		return Long.valueOf(claims.getSubject());
	}

	public String createCouncilAccessToken(Long councilId) {
		Instant now = Instant.now();
		return Jwts.builder()
			.subject(String.valueOf(councilId))
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusSeconds(accessTokenExpirationSeconds)))
			.claim("role", "COUNCIL")
			.signWith(accessKey)
			.compact();
	}

	public String createCouncilRefreshToken(Long councilId) {
		Instant now = Instant.now();
		return Jwts.builder()
			.subject(String.valueOf(councilId))
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusSeconds(refreshTokenExpirationSeconds)))
			.claim("role", "COUNCIL")
			.signWith(refreshKey)
			.compact();
	}

	public Long getCouncilIdFromAccessToken(String token) {
		Claims claims = jwtAuthenticator.parseAccessToken(token).getPayload();
		String role = claims.get("role", String.class);
		if (!"COUNCIL".equals(role)) {
			throw new InvalidJwtException();
		}
		return Long.valueOf(claims.getSubject());
	}
}
