package com.campus.campus.global.config;

import org.springframework.stereotype.Component;

@Component
public class PermitUrlConfig {
	public String[] getPublicUrl() {
		return new String[] {
			"/v3/api-docs/**",
			"/swagger-ui/**",
			"/health-check",
			"/actuator/health",
			"/auth/login/kakao",
			"/auth/council/signup",
			"/auth/council/login",
			"/auth/council/find/id",
			"/auth/council/find/password",
			"/auth/council/signup/email/code",
			"/auth/council/signup/email/code/verify",
			"/auth/council/find/id/email/code",
			"/auth/council/find/id/email/code/verify",
			"/auth/council/find/password/email/code",
			"/auth/council/find/password/email/code/verify",
			"/search/schools",
			"/search/colleges",
			"/search/majors",
			"/jwt/token/reissue",
			"/places/search"
		};
	}
}
