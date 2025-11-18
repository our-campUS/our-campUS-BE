package com.campus.campus.domain.user.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.user.application.dto.response.UserFirstProfileResponse;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
	public User createUser(Long kakaoId, String nickname, String email, String profileImage) {
		return User.builder()
			.kakaoId(kakaoId)
			.nickname(nickname)
			.email(email)
			.profileImage(profileImage)
			.build();
	}

	public UserFirstProfileResponse toUserFirstProfileResponse(User user) {
		return UserFirstProfileResponse.builder()
			.schoolName(user.getSchool().getSchoolName())
			.collegeName(user.getCollege().getCollegeName())
			.majorName(user.getMajor().getMajorName())
			.build();
	}
}
