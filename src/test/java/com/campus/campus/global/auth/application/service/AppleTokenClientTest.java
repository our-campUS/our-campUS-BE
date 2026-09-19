package com.campus.campus.global.auth.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.campus.campus.global.auth.application.dto.AppleTokenResponse;
import com.campus.campus.global.auth.application.property.AppleOauthProperty;
import com.campus.campus.global.auth.exception.AppleTokenExchangeException;

@ExtendWith(MockitoExtension.class)
class AppleTokenClientTest {

	@Mock
	private AppleClientSecretGenerator clientSecretGenerator;

	private MockRestServiceServer server;
	private AppleTokenClient appleTokenClient;

	@BeforeEach
	void setUp() {
		AppleOauthProperty property = new AppleOauthProperty();
		property.setClientId("com.campus.app");

		RestClient.Builder restClientBuilder = RestClient.builder();
		server = MockRestServiceServer.bindTo(restClientBuilder).build();
		appleTokenClient = new AppleTokenClient(restClientBuilder.build(), property, clientSecretGenerator);
	}

	@Test
	void exchangeAuthorizationCode_인증_코드를_Apple_토큰으로_교환한다() {
		when(clientSecretGenerator.generate()).thenReturn("client-secret");
		server.expect(requestTo("https://appleid.apple.com/auth/token"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(content().string(allOf(
				containsString("client_id=com.campus.app"),
				containsString("client_secret=client-secret"),
				containsString("code=authorization-code"),
				containsString("grant_type=authorization_code")
			)))
			.andRespond(withSuccess("""
				{
				  "access_token": "apple-access-token",
				  "token_type": "Bearer",
				  "expires_in": 3600,
				  "refresh_token": "apple-refresh-token",
				  "id_token": "apple-id-token"
				}
				""", MediaType.APPLICATION_JSON));

		AppleTokenResponse response = appleTokenClient.exchangeAuthorizationCode("authorization-code");

		assertThat(response.idToken()).isEqualTo("apple-id-token");
		assertThat(response.refreshToken()).isEqualTo("apple-refresh-token");
		server.verify();
	}

	@Test
	void exchangeAuthorizationCode_Apple이_오류를_응답하면_예외를_던진다() {
		when(clientSecretGenerator.generate()).thenReturn("client-secret");
		server.expect(requestTo("https://appleid.apple.com/auth/token"))
			.andRespond(withBadRequest());

		assertThatThrownBy(() -> appleTokenClient.exchangeAuthorizationCode("invalid-code"))
			.isInstanceOf(AppleTokenExchangeException.class);
		server.verify();
	}
}
