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
			"/auth/council/signup/validate",
			"/auth/council/login",
			"/auth/council/find/id",
			"/auth/council/find/password",
			"/auth/council/find/password/validate/id",
			"/auth/council/find/password/validate/email",
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
			"/managers/login",
			"/places/search",
			"/places/search/info",
			"/storage/presigned",
			"/places",
			"/api/partnership/list",
			"/api/partnership/map",
			// TODO: 스토어 심사 후 제거
			"/test/**",
			// TODO: main 브랜치 머지 전 반드시 제거 — dev 전용 엔드포인트
			"/dev/**"
		};
	}
}
