package com.campus.campus.domain.user.domain.entity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;

class UserTest {

	@Test
	void scrubPersonalInfo_클릭_시_재식별_가능한_필드만_지운다() {
		School school = mock(School.class);
		College college = mock(College.class);
		Major major = mock(Major.class);

		User user = User.builder()
			.kakaoId(123456789L)
			.appleId("001234.abcdef")
			.appleRefreshToken("apple-refresh-token")
			.nickname("홍길동")
			.email("hong@example.com")
			.profileImage("https://example.com/profile.png")
			.campusNickname("길동이")
			.school(school)
			.college(college)
			.major(major)
			.build();

		user.scrubPersonalInfo();

		assertThat(user.getKakaoId()).isNull();
		assertThat(user.getAppleId()).isNull();
		assertThat(user.getAppleRefreshToken()).isNull();
		assertThat(user.getEmail()).isNull();
		assertThat(user.getProfileImage()).isNull();
		assertThat(user.getNickname()).isEqualTo("---");
		assertThat(user.getCampusNickname()).isEqualTo("---");
	}

	@Test
	void scrubPersonalInfo_호출해도_학적_정보는_유지된다() {
		School school = mock(School.class);
		College college = mock(College.class);
		Major major = mock(Major.class);

		User user = User.builder()
			.kakaoId(123456789L)
			.school(school)
			.college(college)
			.major(major)
			.build();

		user.scrubPersonalInfo();

		assertThat(user.getSchool()).isSameAs(school);
		assertThat(user.getCollege()).isSameAs(college);
		assertThat(user.getMajor()).isSameAs(major);
	}
}
