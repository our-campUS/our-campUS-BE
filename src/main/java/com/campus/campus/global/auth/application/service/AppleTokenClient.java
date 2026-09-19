package com.campus.campus.global.auth.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.campus.campus.global.auth.application.dto.AppleTokenResponse;
import com.campus.campus.global.auth.application.property.AppleOauthProperty;
import com.campus.campus.global.auth.exception.AppleTokenExchangeException;

@Component
public class AppleTokenClient {

	private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
	private static final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";

	private final RestClient restClient;
	private final AppleOauthProperty appleOauthProperty;
	private final AppleClientSecretGenerator clientSecretGenerator;

	public AppleTokenClient(
		@Qualifier("restClient") RestClient restClient,
		AppleOauthProperty appleOauthProperty,
		AppleClientSecretGenerator clientSecretGenerator
	) {
		this.restClient = restClient;
		this.appleOauthProperty = appleOauthProperty;
		this.clientSecretGenerator = clientSecretGenerator;
	}

	public AppleTokenResponse exchangeAuthorizationCode(String authorizationCode) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", appleOauthProperty.getClientId());
		body.add("client_secret", clientSecretGenerator.generate());
		body.add("code", authorizationCode);
		body.add("grant_type", AUTHORIZATION_CODE_GRANT_TYPE);

		try {
			AppleTokenResponse response = restClient.post()
				.uri(APPLE_TOKEN_URL)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(body)
				.retrieve()
				.body(AppleTokenResponse.class);

			if (response == null
				|| !StringUtils.hasText(response.idToken())
				|| !StringUtils.hasText(response.refreshToken())) {
				throw new AppleTokenExchangeException();
			}

			return response;
		} catch (RestClientException e) {
			throw new AppleTokenExchangeException();
		}
	}
}
