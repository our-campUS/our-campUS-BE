package com.campus.campus.domain.user.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.campus.campus.domain.councilpost.domain.repository.LikePostRepository;
import com.campus.campus.domain.notification.domain.repository.NotificationRepository;
import com.campus.campus.domain.place.domain.repository.LikedPlacesRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.auth.application.service.AppleTokenClient;
import com.campus.campus.global.auth.application.service.AppleTokenEncryptor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAnonymizationService {

	private final UserRepository userRepository;
	private final KakaoOauthService kakaoOauthService;
	private final AppleTokenClient appleTokenClient;
	private final NotificationRepository notificationRepository;
	private final LikePostRepository likePostRepository;
	private final LikedPlacesRepository likedPlacesRepository;
	private final AppleTokenEncryptor appleTokenEncryptor;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean anonymize(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (user.getKakaoId() != null) {
			boolean unlinked = kakaoOauthService.unlink(user.getKakaoId());
			if (!unlinked) {
				return false;
			}
		}

		String encryptedRefreshToken = user.encryptedAppleRefreshTokenForRevocation();

		if (StringUtils.hasText(encryptedRefreshToken)) {
			String refreshToken = appleTokenEncryptor.decrypt(encryptedRefreshToken);
			boolean revoked = appleTokenClient.revoke(refreshToken);
			if (!revoked) {
				return false;
			}
		}

		deleteHardDeleteTargets(user.getId());

		user.scrubPersonalInfo();
		user.delete(LocalDateTime.now());
		return true;
	}

	// Review/Stamp/Reward/Inquiry/UserPartnershipSuggestion은 익명화 유지 대상이라 여기서 지우지 않는다.
	// User가 scrubPersonalInfo()로 스크럽되면 자동으로 "탈퇴한 사용자"를 가리키게 되므로 별도 처리가 필요 없다.
	private void deleteHardDeleteTargets(Long userId) {
		notificationRepository.deleteAllByUserId(userId);
		likePostRepository.deleteAllByUserId(userId);
		likedPlacesRepository.deleteAllByUserId(userId);
	}
}
