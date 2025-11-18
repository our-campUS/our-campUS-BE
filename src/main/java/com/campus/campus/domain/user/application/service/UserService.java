package com.campus.campus.domain.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.user.application.dto.request.UserProfileRequest;
import com.campus.campus.domain.user.application.exception.UserNotFirstLoginException;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public void writeUserProfile(Long userId, UserProfileRequest userProfileRequest) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (!user.isProfileNotCompleted()) {
			throw new UserNotFirstLoginException();
		}

		user.updateProfile(userProfileRequest.school(), userProfileRequest.college(), userProfileRequest.major());
	}
}
