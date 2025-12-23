package com.campus.campus.domain.user.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.global.auth.application.mapper.LoginMapper;
import com.campus.campus.domain.user.application.mapper.UserMapper;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.auth.application.dto.KakaoTokenResponse;
import com.campus.campus.global.auth.application.dto.KakaoUserResponse;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.global.auth.application.property.KakaoOauthProperty;
import com.campus.campus.global.util.jwt.application.service.RedisTokenService;
import com.campus.campus.global.util.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class KakaoOauthService {

	private static final String KAUTH_BASE_URL = "https://kauth.kakao.com";
	private static final String KAPI_BASE_URL = "https://kapi.kakao.com";
	private static final String UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

	private final KakaoOauthProperty kakaoOauthProperty;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final RedisTokenService redisTokenService;

	private final LoginMapper loginMapper;
	private final UserMapper userMapper;

	@Value("${jwt.refresh.expiration-seconds}") // yml에서 값 가져오기
	private long refreshTokenExpirationSeconds;

	@Transactional
	public OauthLoginResponse login(String authorizationCode) {
		KakaoTokenResponse kakaoToken = getToken(authorizationCode);
		KakaoUserResponse kakaoUser = getUserInfo(kakaoToken.accessToken());

		User user = findOrCreateUser(kakaoUser);

		String accessToken = jwtProvider.createAccessToken(user.getId());
		String refreshToken = jwtProvider.createRefreshToken(user.getId());

		redisTokenService.setRefreshToken("USER", String.valueOf(user.getId()), refreshToken, refreshTokenExpirationSeconds);

		return loginMapper.toOauthLoginResponse(user, accessToken, refreshToken);
	}

	@Transactional
	public void withdraw(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (user.getKakaoId() != null) {
			unlink(user.getKakaoId());
		}

		userRepository.delete(user);
	}

	private KakaoTokenResponse getToken(String authorizationCode) {
		RestClient client = RestClient.create();

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", kakaoOauthProperty.getClientId());
		body.add("redirect_uri", kakaoOauthProperty.getRedirectUri());
		body.add("code", authorizationCode);

		if (kakaoOauthProperty.getClientSecret() != null &&
			!kakaoOauthProperty.getClientSecret().isBlank()) {
			body.add("client_secret", kakaoOauthProperty.getClientSecret());
		}

		return client.post()
			.uri(KAUTH_BASE_URL + "/oauth/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(body)
			.retrieve()
			.body(KakaoTokenResponse.class);
	}

	private KakaoUserResponse getUserInfo(String kakaoAccessToken) {
		RestClient client = RestClient.create();

		return client.post()
			.uri(KAPI_BASE_URL + "/v2/user/me")
			.header("Authorization", "Bearer " + kakaoAccessToken)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.body(KakaoUserResponse.class);
	}

	private User findOrCreateUser(KakaoUserResponse kakaoUserResponse) {
		Long kakaoId = kakaoUserResponse.id();

		String email = kakaoUserResponse.kakaoAccount() != null ? kakaoUserResponse.kakaoAccount().email() : null;

		String nickname = (kakaoUserResponse.kakaoAccount() != null &&
			kakaoUserResponse.kakaoAccount().profile() != null &&
			kakaoUserResponse.kakaoAccount().profile().nickname() != null)
			? kakaoUserResponse.kakaoAccount().profile().nickname()
			: "kakao_" + kakaoId;

		String profileImage = (kakaoUserResponse.kakaoAccount() != null &&
			kakaoUserResponse.kakaoAccount().profile() != null)
			? kakaoUserResponse.kakaoAccount().profile().profileImageUrl()
			: null;

		return userRepository.findByKakaoId(kakaoId)
			.orElseGet(() -> {
				User newUser = userMapper.createUser(kakaoId, nickname, email, profileImage);
				return userRepository.save(newUser);
			});
	}

	private void unlink(Long kakaoId) {
		RestClient client = RestClient.create();

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("target_id_type", "user_id");
		body.add("target_id", String.valueOf(kakaoId));

		try {
			client.post()
				.uri(UNLINK_URL)
				.header("Authorization", "KakaoAK " + kakaoOauthProperty.getAdminKey())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(body)
				.retrieve()
				.toBodilessEntity();
		} catch (Exception e) {
			System.err.println("카카오 연결 끊기 실패: " + e.getMessage());
		}
	}
}
