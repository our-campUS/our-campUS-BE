package com.campus.campus.global.util.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.manager.application.exception.ManagerNotFoundException;
import com.campus.campus.domain.manager.domain.entity.Manager;
import com.campus.campus.domain.manager.domain.repository.ManagerRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.util.jwt.application.service.RedisTokenService;
import com.campus.campus.global.util.jwt.exception.ExpireJwtException;
import com.campus.campus.global.util.jwt.exception.InvalidJwtException;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtAuthenticator jwtAuthenticator;
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final StudentCouncilRepository studentCouncilRepository;
	private final ManagerRepository managerRepository;
	private final RedisTokenService redisTokenService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String token = resolveToken(request);

		if (token != null) {
			try {
				if (redisTokenService.hasKeyBlackList(token)) {
					throw new InvalidJwtException();
				}
				Authentication authentication = createAuthentication(token, request);

				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (InvalidJwtException | ExpireJwtException e) {
				log.warn("JWT validation failed: {}", e.getMessage());
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

		Claims claims = jwtAuthenticator.parseAccessToken(token).getPayload();
		String role = claims.get("role", String.class);
		String subject = claims.getSubject();

		UserDetails principal;
		if ("USER".equals(role)) {
			Long userId = Long.valueOf(subject);
			User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(UserNotFoundException::new);
			principal = UserPrincipal.from(user);
		} else if ("COUNCIL".equals(role)) {
			Long councilId = Long.valueOf(subject);
			StudentCouncil council = studentCouncilRepository.findByIdAndDeletedAtIsNull(councilId)
				.orElseThrow(StudentCouncilNotFoundException::new);
			principal = StudentCouncilPrincipal.from(council);
		} else if ("MANAGER".equals(role)) {
			Long managerId = Long.valueOf(subject);
			Manager manager = managerRepository.findById(managerId)
				.orElseThrow(ManagerNotFoundException::new);
			principal = ManagerPrincipal.from(manager);
		} else {
			throw new InvalidJwtException();
		}

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(
				principal,
				null,
				principal.getAuthorities()
			);

		authentication.setDetails(
			new WebAuthenticationDetailsSource().buildDetails(request)
		);

		return authentication;
	}
}
