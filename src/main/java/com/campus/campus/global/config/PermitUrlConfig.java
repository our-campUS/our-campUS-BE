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
			"/auth/council/signup/email/code",
			"/auth/council/signup/email/code/verify",
			"/schools/search"
		};
	}
}
