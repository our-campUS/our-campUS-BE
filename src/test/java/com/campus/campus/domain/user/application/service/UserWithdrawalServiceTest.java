package com.campus.campus.domain.user.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.campus.campus.domain.user.application.exception.NicknameNotMatchException;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserWithdrawalServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserWithdrawalService userWithdrawalService;

	@Test
	void withdraw_사용자를_soft_delete한다() {
		User user = User.builder().id(1L).nickname("홍길동").build();
		when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

		userWithdrawalService.withdraw(1L, "홍길동");

		assertThat(user.getDeletedAt()).isNotNull();
		verify(userRepository).save(user);
	}

	@Test
	void withdraw_사용자가_없으면_예외를_던진다() {
		when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userWithdrawalService.withdraw(1L, "홍길동"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	void withdraw_이름이_일치하지_않으면_예외를_던진다() {
		User user = User.builder().id(1L).nickname("홍길동").build();
		when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> userWithdrawalService.withdraw(1L, "다른 이름"))
			.isInstanceOf(NicknameNotMatchException.class);
		verify(userRepository, never()).save(any());
	}
}
