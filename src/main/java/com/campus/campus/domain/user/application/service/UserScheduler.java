package com.campus.campus.domain.user.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserScheduler {
	private final UserRepository userRepository;
	private final KakaoOauthService kakaoOauthService;

	@Scheduled(cron = "0 45 21 ? * WED", zone = "Asia/Seoul")
	@Transactional
	public void cleanupDeletedUsers() {
		LocalDateTime softDeleteDate = LocalDateTime.now().minusWeeks(1);
		List<User> users = userRepository.findAllByDeletedAtIsNotNullAndDeletedAtBefore(softDeleteDate);

		for (User user : users) {
			boolean unlinked = true;
			if (user.getKakaoId() != null) {
				unlinked = kakaoOauthService.unlink(user.getKakaoId());
			}

			if (unlinked) {
				userRepository.delete(user);
			}
		}
	}
}
