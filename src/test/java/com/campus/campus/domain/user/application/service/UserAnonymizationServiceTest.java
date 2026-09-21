package com.campus.campus.domain.user.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.campus.campus.domain.councilpost.domain.repository.LikePostRepository;
import com.campus.campus.domain.notification.domain.repository.NotificationRepository;
import com.campus.campus.domain.place.domain.repository.LikedPlacesRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.auth.application.service.AppleTokenClient;
import com.campus.campus.global.auth.application.service.AppleTokenEncryptor;

@ExtendWith(MockitoExtension.class)
class UserAnonymizationServiceTest {

	private static final String ENCRYPTED_APPLE_REFRESH_TOKEN =
		"v1.encoded-iv.encoded-ciphertext";
	private static final String APPLE_REFRESH_TOKEN =
		"apple-refresh-token";

	@Mock
	private UserRepository userRepository;

	@Mock
	private KakaoOauthService kakaoOauthService;

	@Mock
	private AppleTokenClient appleTokenClient;

	@Mock
	private AppleTokenEncryptor appleTokenEncryptor;

	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private LikePostRepository likePostRepository;

	@Mock
	private LikedPlacesRepository likedPlacesRepository;

	@InjectMocks
	private UserAnonymizationService userAnonymizationService;

	@Test
	void 존재하지_않는_유저면_예외를_던진다() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userAnonymizationService.anonymize(1L))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	void 카카오_unlink_실패하면_아무것도_하지_않고_false를_반환한다() {
		User user = User.builder().id(1L).kakaoId(999L).nickname("홍길동").build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(kakaoOauthService.unlink(999L)).thenReturn(false);

		boolean result = userAnonymizationService.anonymize(1L);

		assertThat(result).isFalse();
		assertThat(user.getNickname()).isEqualTo("홍길동");
		verifyNoInteractions(notificationRepository, likePostRepository, likedPlacesRepository);
		verify(userRepository, never()).save(any());
	}

	@Test
	void 카카오_unlink_성공하면_하드삭제_대상을_지우고_유저를_스크럽한다() {
		User user = User.builder().id(1L).kakaoId(999L).nickname("홍길동").campusNickname("길동이").build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(kakaoOauthService.unlink(999L)).thenReturn(true);

		boolean result = userAnonymizationService.anonymize(1L);

		assertThat(result).isTrue();
		assertThat(user.getNickname()).isEqualTo("---");
		assertThat(user.getCampusNickname()).isEqualTo("---");
		verify(notificationRepository).deleteAllByUserId(1L);
		verify(likePostRepository).deleteAllByUserId(1L);
		verify(likedPlacesRepository).deleteAllByUserId(1L);
	}

	@Test
	void Apple_연결_해제에_실패하면_사용자_정보를_유지하고_false를_반환한다() {
		User user = User.builder()
			.id(1L)
			.appleId("apple-user-id")
			.encryptedAppleRefreshToken(ENCRYPTED_APPLE_REFRESH_TOKEN)
			.nickname("홍길동")
			.build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(appleTokenEncryptor.decrypt(ENCRYPTED_APPLE_REFRESH_TOKEN)).thenReturn(APPLE_REFRESH_TOKEN);
		when(appleTokenClient.revoke(APPLE_REFRESH_TOKEN)).thenReturn(false);

		boolean result = userAnonymizationService.anonymize(1L);

		assertThat(result).isFalse();
		assertThat(user.getNickname()).isEqualTo("홍길동");
		assertThat(user.encryptedAppleRefreshTokenForRevocation()).isEqualTo(ENCRYPTED_APPLE_REFRESH_TOKEN);
		verify(appleTokenEncryptor).decrypt(ENCRYPTED_APPLE_REFRESH_TOKEN);
		verify(appleTokenClient).revoke(APPLE_REFRESH_TOKEN);
		verifyNoInteractions(notificationRepository, likePostRepository, likedPlacesRepository);
		verify(userRepository, never()).save(any());
	}

	@Test
	void Apple_연결_해제에_성공하면_하드삭제_대상을_지우고_유저를_스크럽한다() {
		User user = User.builder()
			.id(1L)
			.appleId("apple-user-id")
			.encryptedAppleRefreshToken(ENCRYPTED_APPLE_REFRESH_TOKEN)
			.nickname("홍길동")
			.build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(appleTokenEncryptor.decrypt(ENCRYPTED_APPLE_REFRESH_TOKEN)).thenReturn(APPLE_REFRESH_TOKEN);
		when(appleTokenClient.revoke("apple-refresh-token")).thenReturn(true);

		boolean result = userAnonymizationService.anonymize(1L);

		assertThat(result).isTrue();
		assertThat(user.getAppleId()).isNull();
		assertThat(user.encryptedAppleRefreshTokenForRevocation()).isNull();
		verify(appleTokenEncryptor).decrypt(ENCRYPTED_APPLE_REFRESH_TOKEN);
		verify(appleTokenClient).revoke(APPLE_REFRESH_TOKEN);
		verify(notificationRepository).deleteAllByUserId(1L);
		verify(likePostRepository).deleteAllByUserId(1L);
		verify(likedPlacesRepository).deleteAllByUserId(1L);
	}

	@Test
	void 소셜_연동이_없는_유저는_연결_해제_없이_바로_스크럽한다() {
		User user = User.builder().id(1L).kakaoId(null).nickname("홍길동").build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		boolean result = userAnonymizationService.anonymize(1L);

		assertThat(result).isTrue();
		assertThat(user.getNickname()).isEqualTo("---");
		verifyNoInteractions(kakaoOauthService, appleTokenClient, appleTokenEncryptor);
	}
}
