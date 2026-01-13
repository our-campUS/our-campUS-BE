package com.campus.campus.domain.stamp.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.stamp.application.mapper.StampMapper;
import com.campus.campus.domain.stamp.domain.entity.Stamp;
import com.campus.campus.domain.stamp.domain.repository.StampRepository;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StampService {
	private final StampRepository stampRepository;
	private final StampMapper stampMapper;

	@Transactional
	public void grantStampForReview(User user, Review review) {
		if (stampRepository.existsByReview(review)) {
			return;
		}

		Stamp stamp = stampMapper.createStamp(user, review);
		stampRepository.save(stamp);

		int currentStampCount = stampRepository.countByUser(user);
		if (currentStampCount >= 10 && !user.isRewardNeeded()) {
			user.updateRewardNeeded(true);
		}
	}
}
