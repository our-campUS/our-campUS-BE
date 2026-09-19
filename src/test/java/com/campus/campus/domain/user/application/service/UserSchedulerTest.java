package com.campus.campus.domain.user.application.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserSchedulerTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserAnonymizationService userAnonymizationService;

	@InjectMocks
	private UserScheduler userScheduler;

	@Test
	void 유예기간_지난_유저들을_각각_익명화한다() {
		User user1 = User.builder().id(1L).build();
		User user2 = User.builder().id(2L).build();
		when(userRepository.findAllByDeletedAtIsNotNullAndDeletedAtBefore(any(LocalDateTime.class)))
			.thenReturn(List.of(user1, user2));

		userScheduler.cleanupDeletedUsers();

		verify(userAnonymizationService).anonymize(1L);
		verify(userAnonymizationService).anonymize(2L);
	}

	@Test
	void 한_유저_처리_중_예외가_나도_나머지_유저_처리는_계속된다() {
		User user1 = User.builder().id(1L).build();
		User user2 = User.builder().id(2L).build();
		when(userRepository.findAllByDeletedAtIsNotNullAndDeletedAtBefore(any(LocalDateTime.class)))
			.thenReturn(List.of(user1, user2));
		when(userAnonymizationService.anonymize(1L)).thenThrow(new RuntimeException("카카오 API 호출 실패"));

		userScheduler.cleanupDeletedUsers();

		verify(userAnonymizationService).anonymize(1L);
		verify(userAnonymizationService).anonymize(2L);
	}
}
