package com.campus.campus.domain.stamp.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.stamp.application.dto.response.RewardResponse;
import com.campus.campus.domain.stamp.domain.entity.Reward;
import com.campus.campus.domain.stamp.domain.entity.Stamp;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StampMapper {
	public RewardResponse toRewardResponse(Reward reward) {
		return new RewardResponse(
			reward.getRewardId(),
			reward.getRewardImageUrl(),
			reward.getCreatedAt()
		);
	}

	public Stamp createStamp(User user, Review review) {
		return Stamp.builder()
			.user(user)
			.review(review)
			.build();
	}
}
