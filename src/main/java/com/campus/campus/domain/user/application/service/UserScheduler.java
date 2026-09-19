package com.campus.campus.domain.user.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserScheduler {
	private static final Logger log = LoggerFactory.getLogger(UserScheduler.class);

	private final UserRepository userRepository;
	private final UserAnonymizationService userAnonymizationService;

	@Scheduled(cron = "0 45 21 ? * WED", zone = "Asia/Seoul")
	public void cleanupDeletedUsers() {
		LocalDateTime softDeleteDate = LocalDateTime.now().minusWeeks(1);
		List<User> users = userRepository.findAllByDeletedAtIsNotNullAndDeletedAtBefore(softDeleteDate);

		for (User user : users) {
			try {
				userAnonymizationService.anonymize(user.getId());
			} catch (Exception e) {
				log.error("회원 익명화 처리 실패, userId={}", user.getId(), e);
			}
		}
	}
}
