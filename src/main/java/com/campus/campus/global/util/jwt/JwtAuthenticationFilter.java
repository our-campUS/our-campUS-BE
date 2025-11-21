package com.campus.campus.global.util.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.util.jwt.exception.ExpireJwtException;
import com.campus.campus.global.util.jwt.exception.InvalidJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtAuthenticator jwtAuthenticator;
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String token = resolveToken(request);

		if (token != null) {
			try {
				Authentication authentication = createAuthentication(token, request);

				if (authentication != null) {
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (InvalidJwtException | ExpireJwtException e) {

			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return null;
		}

		return authHeader.substring(7);
	}

	private Authentication createAuthentication(String token, HttpServletRequest request) {
		jwtAuthenticator.verifyAccessToken(token);

		Long userId = jwtProvider.getUserIdFromAccessToken(token);

		User user = userRepository.findById(userId)
			.orElse(null);

		if (user == null) {
			return null;
		}

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(
				user, // principal
				null,
				List.of(new SimpleGrantedAuthority("ROLE_USER"))
			);

		authentication.setDetails(
			new WebAuthenticationDetailsSource().buildDetails(request)
		);

		return authentication;
	}
}
