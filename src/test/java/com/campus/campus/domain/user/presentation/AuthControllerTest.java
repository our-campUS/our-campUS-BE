package com.campus.campus.domain.user.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.campus.campus.domain.user.application.service.AppleOauthService;
import com.campus.campus.domain.user.application.service.KakaoOauthService;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.global.common.exception.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private KakaoOauthService kakaoOauthService;
	@Mock
	private AppleOauthService appleOauthService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		AuthController authController = new AuthController(kakaoOauthService, appleOauthService);
		mockMvc = MockMvcBuilders.standaloneSetup(authController)
			.setControllerAdvice(new GlobalExceptionHandler())
			.setValidator(validator)
			.build();
	}

	@Test
	void appleLogin_인증_코드로_로그인한다() throws Exception {
		OauthLoginResponse loginResponse = new OauthLoginResponse(
			"access-token",
			"refresh-token",
			"홍길동",
			null,
			1L,
			null,
			"user@example.com",
			null,
			true
		);
		when(appleOauthService.login("authorization-code", "홍길동"))
			.thenReturn(loginResponse);

		mockMvc.perform(post("/auth/login/apple")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "authorizationCode": "authorization-code",
					  "nickname": "홍길동"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.accessToken").value("access-token"))
			.andExpect(jsonPath("$.data.userId").value(1L));

		verify(appleOauthService).login("authorization-code", "홍길동");
	}

	@Test
	void appleLogin_인증_코드가_비어있으면_요청을_거부한다() throws Exception {
		mockMvc.perform(post("/auth/login/apple")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "authorizationCode": "",
					  "nickname": "홍길동"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(4001));

		verifyNoInteractions(appleOauthService);
	}
}
