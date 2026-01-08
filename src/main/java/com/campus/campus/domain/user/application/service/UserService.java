package com.campus.campus.domain.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.school.application.exception.MajorNotFoundException;
import com.campus.campus.domain.school.application.exception.SchoolMajorNotSameException;
import com.campus.campus.domain.school.application.exception.SchoolNotFoundException;
import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;
import com.campus.campus.domain.school.domain.repository.MajorRepository;
import com.campus.campus.domain.school.domain.repository.SchoolRepository;
import com.campus.campus.domain.user.application.dto.request.CampusNicknameUpdateRequest;
import com.campus.campus.domain.user.application.dto.request.UserProfileRequest;
import com.campus.campus.domain.user.application.dto.response.UserFirstProfileResponse;
import com.campus.campus.domain.user.application.dto.response.UserInfoResponse;
import com.campus.campus.domain.user.application.exception.NicknameAlreadyExistsException;
import com.campus.campus.domain.user.application.exception.UserNotFirstLoginException;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.application.mapper.UserMapper;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final SchoolRepository schoolRepository;
	private final MajorRepository majorRepository;
	private final UserMapper userMapper;

	@Transactional
	public UserFirstProfileResponse writeUserProfile(Long userId, UserProfileRequest userProfileRequest) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		if (!user.isProfileNotCompleted()) {
			throw new UserNotFirstLoginException();
		}

		School school = schoolRepository.findById(userProfileRequest.schoolId())
			.orElseThrow(SchoolNotFoundException::new);
		Major major = majorRepository.findById(userProfileRequest.majorId())
			.orElseThrow(MajorNotFoundException::new);

		if (!major.getSchool().getSchoolId().equals(school.getSchoolId())) {
			throw new SchoolMajorNotSameException();
		}

		College college = major.getCollege();

		user.updateProfile(school, college, major);

		return userMapper.toUserFirstProfileResponse(user);
	}

	@Transactional
	public void updateCampusNickname(Long userId, CampusNicknameUpdateRequest nicknameUpdateRequest) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		if (userRepository.existsByCampusNicknameAndIdNot(nicknameUpdateRequest.campusNickname(), userId)) {
			throw new NicknameAlreadyExistsException();
		}

		user.updateCampusNickname(nicknameUpdateRequest.campusNickname());
		userRepository.save(user);
	}

	public UserInfoResponse getUserInfo(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		return userMapper.toUserInfoResponse(user);
	}
}
