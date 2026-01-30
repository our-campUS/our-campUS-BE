package com.campus.campus.domain.user.application.service;

import java.time.LocalDateTime;

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
import com.campus.campus.domain.user.application.dto.request.ChangeProfileImageRequest;
import com.campus.campus.domain.user.application.dto.request.ChangeUserAcademicRequest;
import com.campus.campus.domain.user.application.dto.request.UserProfileRequest;
import com.campus.campus.domain.user.application.dto.response.ChangeProfileImageResponse;
import com.campus.campus.domain.user.application.dto.response.ChangeUserAcademicResponse;
import com.campus.campus.domain.user.application.dto.response.UserFirstProfileResponse;
import com.campus.campus.domain.user.application.dto.response.UserInfoIdsResponse;
import com.campus.campus.domain.user.application.dto.response.UserInfoResponse;
import com.campus.campus.domain.user.application.exception.AcademicInfoUpdateRestrictionException;
import com.campus.campus.domain.user.application.exception.CauOnlyException;
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
	private static final Long CAU_SCHOOL_ID = 285L;

	@Transactional
	public UserFirstProfileResponse writeUserProfile(Long userId, UserProfileRequest userProfileRequest) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		if (!user.isProfileNotCompleted()) {
			throw new UserNotFirstLoginException();
		}

		if (!CAU_SCHOOL_ID.equals(userProfileRequest.schoolId())) {
			throw new CauOnlyException();
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

	@Transactional
	public ChangeProfileImageResponse updateProfileImage(Long userId, ChangeProfileImageRequest profileImageRequest) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		user.updateProfileImage(profileImageRequest.newProfileImage());
		userRepository.save(user);

		return userMapper.toChangeProfileImageResponse(user);
	}

	@Transactional
	public ChangeUserAcademicResponse updateUserAcademic(Long userId, ChangeUserAcademicRequest userAcademicRequest) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		if (user.getLastProfileUpdatedAt() != null) {
			LocalDateTime nextAvailableDate = user.getLastProfileUpdatedAt().plusMonths(3);
			if (LocalDateTime.now().isBefore(nextAvailableDate)) {
				throw new AcademicInfoUpdateRestrictionException();
			}
		}

		if (!CAU_SCHOOL_ID.equals(userAcademicRequest.schoolId())) {
			throw new CauOnlyException();
		}

		School school = schoolRepository.findById(userAcademicRequest.schoolId())
			.orElseThrow(SchoolNotFoundException::new);
		Major major = majorRepository.findById(userAcademicRequest.majorId())
			.orElseThrow(MajorNotFoundException::new);

		if (!major.getSchool().getSchoolId().equals(school.getSchoolId())) {
			throw new SchoolMajorNotSameException();
		}

		College college = major.getCollege();
		user.updateProfile(school, college, major);
		userRepository.save(user);

		return userMapper.toChangeUserAcademicResponse(user);
	}

	public UserInfoResponse getUserInfo(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		return userMapper.toUserInfoResponse(user);
	}

	public UserInfoIdsResponse getUserInfoIds(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		return userMapper.toUserInfoIdsResponse(user);
	}

}
