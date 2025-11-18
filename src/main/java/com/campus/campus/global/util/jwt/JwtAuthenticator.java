package com.campus.campus.global.util.jwt;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.campus.campus.global.util.jwt.exception.ExpireJwtException;
import com.campus.campus.global.util.jwt.exception.InvalidJwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Component
public class JwtAuthenticator {

	private final String accessSecret;
	private final String refreshSecret;

	@Getter
	private SecretKey accessKey;
	@Getter
	private SecretKey refreshKey;

	public JwtAuthenticator(
		@Value("${jwt.access.secret}") String accessSecret,
		@Value("${jwt.refresh.secret}") String refreshSecret
	) {
		this.accessSecret = accessSecret;
		this.refreshSecret = refreshSecret;
	}

	@PostConstruct
	void init() {
		this.accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(accessSecret));
		this.refreshKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(refreshSecret));
	}

	public Jws<Claims> parseAccessToken(String token) {
		return Jwts.parser()
			.verifyWith(accessKey)
			.build()
			.parseSignedClaims(token);
	}

	public Jws<Claims> parseRefreshToken(String token) {
		return Jwts.parser()
			.verifyWith(refreshKey)
			.build()
			.parseSignedClaims(token);
	}

	public void verifyAccessToken(String token) {
		try {
			parseAccessToken(token);
		} catch (SignatureException | DeserializationException | MalformedJwtException e) {
			throw new InvalidJwtException();
		} catch (ExpiredJwtException e) {
			throw new ExpireJwtException();
		}
	}

	public void verifyRefreshToken(String token) {
		try {
			parseRefreshToken(token);
		} catch (SignatureException | DeserializationException | MalformedJwtException e) {
			throw new InvalidJwtException();
		} catch (ExpiredJwtException e) {
			throw new ExpireJwtException();
		}
	}

}
