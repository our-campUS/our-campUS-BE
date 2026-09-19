package com.campus.campus.domain.user.application.mapper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.campus.campus.domain.user.domain.entity.User;

class UserMapperTest {

	private final UserMapper userMapper = new UserMapper();

	@Test
	void createAppleUser_애플_식별자와_사용자_정보로_회원을_생성한다() {
		User user = userMapper.createAppleUser(
			"001234.abcdef",
			"홍길동",
			"hong@example.com",
			"apple-refresh-token"
		);

		assertThat(user.getAppleId()).isEqualTo("001234.abcdef");
		assertThat(user.getNickname()).isEqualTo("홍길동");
		assertThat(user.getEmail()).isEqualTo("hong@example.com");
		assertThat(user.getAppleRefreshToken()).isEqualTo("apple-refresh-token");
		assertThat(user.getKakaoId()).isNull();
	}
}
