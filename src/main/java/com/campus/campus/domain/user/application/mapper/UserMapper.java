package com.campus.campus.domain.user.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.user.application.dto.response.ChangeProfileImageResponse;
import com.campus.campus.domain.user.application.dto.response.ChangeUserAcademicResponse;
import com.campus.campus.domain.user.application.dto.response.UserFirstProfileResponse;
import com.campus.campus.domain.user.application.dto.response.UserInfoResponse;
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

	public UserInfoResponse toUserInfoResponse(User user) {
		return new UserInfoResponse(
			user.getId(),
			user.getCampusNickname() == null ? user.getNickname() : user.getCampusNickname(),
			user.getSchool().getSchoolName(),
			user.getCollege().getCollegeName(),
			user.getMajor().getMajorName()
		);
	}

	public ChangeProfileImageResponse toChangeProfileImageResponse(User user) {
		return new ChangeProfileImageResponse(
			user.getId(),
			user.getCampusNickname() == null ? user.getNickname() : user.getCampusNickname(),
			user.getProfileImage()
		);
	}

	public ChangeUserAcademicResponse toChangeUserAcademicResponse(User user) {
		return new ChangeUserAcademicResponse(
			user.getId(),
			user.getCampusNickname() == null ? user.getNickname() : user.getCampusNickname(),
			user.getSchool().getSchoolName(),
			user.getMajor().getCollege().getCollegeName(),
			user.getMajor().getMajorName(),
			user.getLastProfileUpdatedAt().plusMonths(3)
		);
	}
}
