package com.campus.campus.global.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PermitUrlConfigTest {

	@Test
	void getPublicUrl_Apple_로그인_경로를_포함한다() {
		PermitUrlConfig permitUrlConfig = new PermitUrlConfig();

		assertThat(permitUrlConfig.getPublicUrl()).contains("/auth/login/apple");
	}
}
