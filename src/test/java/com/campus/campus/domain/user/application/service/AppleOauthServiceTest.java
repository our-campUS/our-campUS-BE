package com.campus.campus.domain.user.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.campus.campus.domain.user.application.exception.UserSignupForbiddenException;
import com.campus.campus.domain.user.application.mapper.UserMapper;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.auth.application.dto.AppleTokenClaims;
import com.campus.campus.global.auth.application.dto.AppleTokenResponse;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.global.auth.application.mapper.LoginMapper;
import com.campus.campus.global.auth.application.service.AppleIdTokenVerifier;
import com.campus.campus.global.auth.application.service.AppleTokenClient;
import com.campus.campus.global.auth.application.service.AppleTokenEncryptor;
import com.campus.campus.global.util.jwt.JwtProvider;
import com.campus.campus.global.util.jwt.application.service.RedisTokenService;

@ExtendWith(MockitoExtension.class)
class AppleOauthServiceTest {

	private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 1_209_600L;
	private static final String APPLE_REFRESH_TOKEN = "apple-refresh-token";
	private static final String ENCRYPTED_APPLE_REFRESH_TOKEN = "v1.encoded-iv.encoded-ciphertext";

	@Mock
	private AppleTokenClient appleTokenClient;
	@Mock
	private AppleIdTokenVerifier appleIdTokenVerifier;
	@Mock
	private AppleTokenEncryptor appleTokenEncryptor;
	@Mock
	private UserRepository userRepository;
	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private RedisTokenService redisTokenService;
	@Mock
	private LoginMapper loginMapper;
	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private AppleOauthService appleOauthService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(
			appleOauthService,
			"refreshTokenExpirationSeconds",
			REFRESH_TOKEN_EXPIRATION_SECONDS
		);
	}

	@Test
	void login_기존_Apple_사용자에게_서비스_토큰을_발급한다() {
		User user = User.builder().id(1L).appleId("apple-user-id").build();
		OauthLoginResponse expected = mock(OauthLoginResponse.class);
		stubAppleAuthentication();
		when(userRepository.findByAppleIdAndDeletedAtIsNull("apple-user-id"))
			.thenReturn(Optional.of(user));
		when(appleTokenEncryptor.encrypt(APPLE_REFRESH_TOKEN)).thenReturn(ENCRYPTED_APPLE_REFRESH_TOKEN);
		when(jwtProvider.createAccessToken(1L)).thenReturn("access-token");
		when(jwtProvider.createRefreshToken(1L)).thenReturn("refresh-token");
		when(loginMapper.toOauthLoginResponse(user, "access-token", "refresh-token"))
			.thenReturn(expected);

		OauthLoginResponse response = appleOauthService.login("authorization-code", null);

		assertThat(response).isSameAs(expected);
		assertThat(user.encryptedAppleRefreshTokenForRevocation()).isEqualTo(ENCRYPTED_APPLE_REFRESH_TOKEN);

		verify(appleTokenEncryptor).encrypt(APPLE_REFRESH_TOKEN);
		verify(redisTokenService).setRefreshToken(
			"USER",
			"1",
			"refresh-token",
			REFRESH_TOKEN_EXPIRATION_SECONDS
		);
	}

	@Test
	void login_신규_Apple_사용자를_생성한다() {
		User newUser = User.builder()
			.appleId("apple-user-id")
			.encryptedAppleRefreshToken(ENCRYPTED_APPLE_REFRESH_TOKEN)
			.build();

		User savedUser = User.builder()
			.id(1L)
			.appleId("apple-user-id")
			.encryptedAppleRefreshToken(ENCRYPTED_APPLE_REFRESH_TOKEN)
			.build();

		stubAppleAuthentication();

		when(userRepository.findByAppleIdAndDeletedAtIsNull("apple-user-id"))
			.thenReturn(Optional.empty());
		when(userRepository.findByAppleId("apple-user-id")).thenReturn(Optional.empty());
		when(appleTokenEncryptor.encrypt(APPLE_REFRESH_TOKEN)).thenReturn(ENCRYPTED_APPLE_REFRESH_TOKEN);
		when(userMapper.createAppleUser(
			"apple-user-id",
			"apple_apple-user-id",
			"user@example.com",
			ENCRYPTED_APPLE_REFRESH_TOKEN
		)).thenReturn(newUser);
		when(userRepository.save(newUser)).thenReturn(savedUser);
		when(jwtProvider.createAccessToken(1L)).thenReturn("access-token");
		when(jwtProvider.createRefreshToken(1L)).thenReturn("refresh-token");

		appleOauthService.login("authorization-code", " ");

		verify(appleTokenEncryptor).encrypt(APPLE_REFRESH_TOKEN);
		verify(userMapper).createAppleUser("apple-user-id", "apple_apple-user-id", "user@example.com",
			ENCRYPTED_APPLE_REFRESH_TOKEN);
		verify(userRepository).save(newUser);
		verify(loginMapper).toOauthLoginResponse(savedUser, "access-token", "refresh-token");
	}

	@Test
	void login_탈퇴한_Apple_계정이면_재가입을_차단한다() {
		User withdrawnUser = User.builder().id(1L).appleId("apple-user-id").build();
		stubAppleAuthentication();
		when(userRepository.findByAppleIdAndDeletedAtIsNull("apple-user-id"))
			.thenReturn(Optional.empty());
		when(userRepository.findByAppleId("apple-user-id"))
			.thenReturn(Optional.of(withdrawnUser));

		assertThatThrownBy(() -> appleOauthService.login("authorization-code", "홍길동"))
			.isInstanceOf(UserSignupForbiddenException.class);
		verifyNoInteractions(appleTokenEncryptor, jwtProvider, redisTokenService, loginMapper);
	}

	private void stubAppleAuthentication() {
		AppleTokenResponse appleToken = new AppleTokenResponse(
			"apple-access-token",
			"Bearer",
			3600L,
			"apple-refresh-token",
			"apple-id-token"
		);
		AppleTokenClaims appleUser = new AppleTokenClaims("apple-user-id", "user@example.com");

		when(appleTokenClient.exchangeAuthorizationCode("authorization-code"))
			.thenReturn(appleToken);
		when(appleIdTokenVerifier.verify("apple-id-token")).thenReturn(appleUser);
	}
}
