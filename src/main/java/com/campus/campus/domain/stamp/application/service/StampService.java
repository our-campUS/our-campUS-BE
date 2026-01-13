package com.campus.campus.domain.stamp.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.stamp.application.dto.response.RewardResponse;
import com.campus.campus.domain.stamp.application.dto.response.StampInfoResponse;
import com.campus.campus.domain.stamp.application.dto.response.StampReviewResponse;
import com.campus.campus.domain.stamp.application.mapper.StampMapper;
import com.campus.campus.domain.stamp.domain.entity.Stamp;
import com.campus.campus.domain.stamp.domain.repository.RewardRepository;
import com.campus.campus.domain.stamp.domain.repository.StampRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StampService {
	private final StampRepository stampRepository;
	private final RewardRepository rewardRepository;
	private final UserRepository userRepository;
	private final StampMapper stampMapper;

	@Transactional
	public void grantStampForReview(User user, Review review) {
		if (stampRepository.existsByReview(review)) {
			return;
		}

		Stamp stamp = stampMapper.createStamp(user, review);
		stampRepository.save(stamp);

		int currentStampCount = stampRepository.countByUser(user);
		if (currentStampCount % 10 == 0 && currentStampCount!=0 && !user.isRewardNeeded()) {
			user.updateRewardNeeded(true);
		}
	}

	public StampInfoResponse getStampInfo(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		int stampCount = stampRepository.countByUser(user);

		List<StampReviewResponse> reviews = stampRepository.findAllByUserWithReviewAndPlace(user)
			.stream()
			.map(stampMapper::toStampReviewResponse)
			.toList();

		return new StampInfoResponse(stampCount, reviews);
	}

	public List<RewardResponse> findRewards(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		return rewardRepository.findAllByUserOrderByRewardIdDesc(user)
			.stream()
			.map(stampMapper::toRewardResponse)
			.toList();
	}
}
