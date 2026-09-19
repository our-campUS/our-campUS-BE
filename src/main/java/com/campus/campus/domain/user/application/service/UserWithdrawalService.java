package com.campus.campus.domain.user.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.user.application.exception.NicknameNotMatchException;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserWithdrawalService {

	private final UserRepository userRepository;

	@Transactional
	public void withdraw(Long userId, String nickname) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		if (user.getNickname() == null || !user.getNickname().equals(nickname)) {
			throw new NicknameNotMatchException();
		}

		user.delete(LocalDateTime.now());
		userRepository.save(user);
	}
}
