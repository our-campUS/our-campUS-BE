package com.campus.campus.domain.user.application.service;

import org.springframework.stereotype.Service;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.auth.application.dto.KakaoTokenResponse;
import com.campus.campus.global.auth.application.dto.KakaoUserResponse;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.global.auth.application.property.KakaoOauthProperty;
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

	private final KakaoOauthProperty kakaoOauthProperty;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	@Transactional
	public OauthLoginResponse login(String authorizationCode) {
		KakaoTokenResponse kakaoToken = getToken(authorizationCode);
		KakaoUserResponse kakaoUser = getUserInfo(kakaoToken.accessToken());

		User user = findOrCreateUser(kakaoUser);

		String accessToken = jwtProvider.createAccessToken(user.getId());
		String refreshToken = jwtProvider.createRefreshToken(user.getId());

		return OauthLoginResponse.from(user, accessToken, refreshToken);
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
				User newUser = User.createUser(kakaoId, nickname, email, profileImage);
				return userRepository.save(newUser);
			});
	}
}
