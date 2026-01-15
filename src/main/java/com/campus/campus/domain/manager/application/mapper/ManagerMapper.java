package com.campus.campus.domain.manager.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.manager.application.dto.request.RewardGrantedEvent;
import com.campus.campus.domain.manager.application.dto.request.RewardRequest;
import com.campus.campus.domain.manager.application.dto.response.CertifyRequestCouncilResponse;
import com.campus.campus.domain.manager.application.dto.response.CouncilApproveOrDenyResponse;
import com.campus.campus.domain.manager.application.dto.response.CertifyRequestCouncilListResponse;
import com.campus.campus.domain.manager.application.dto.response.ManagerLoginResponse;
import com.campus.campus.domain.manager.application.dto.response.StampRewardNeededUserListResponse;
import com.campus.campus.domain.manager.domain.entity.Manager;
import com.campus.campus.domain.stamp.domain.entity.Reward;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ManagerMapper {
	public ManagerLoginResponse toManagerLoginResponse(Manager manager, String accessToken, String refreshToken) {
		return new ManagerLoginResponse(
			accessToken,
			refreshToken,
			manager.getId(),
			manager.getManagerName()
		);
	}

	public CouncilApproveOrDenyResponse toCouncilApproveOrDenyResponse(Long councilId, boolean certifyResult,
		String councilPresident) {
		return new CouncilApproveOrDenyResponse(
			councilId,
			certifyResult,
			councilPresident
		);
	}

	public CertifyRequestCouncilListResponse toCertifyRequestCouncilListResponse(StudentCouncil studentCouncil) {
		return new CertifyRequestCouncilListResponse(
			studentCouncil.getId(),
			studentCouncil.getCouncilName(),
			studentCouncil.getCreatedAt()
		);
	}

	public CertifyRequestCouncilResponse toCertifyRequestCouncilResponse(StudentCouncil studentCouncil) {
		return new CertifyRequestCouncilResponse(
			studentCouncil.getId(),
			studentCouncil.getCouncilName(),
			studentCouncil.getElectionImageUrl()
		);
	}

	public StampRewardNeededUserListResponse toStampRewardNeededUserListResponse(User user, int stampCount) {
		String nickname = user.getCampusNickname();
		if (nickname == null || nickname.isBlank()) {
			nickname = user.getNickname();
		}
		return new StampRewardNeededUserListResponse(
			user.getId(),
			nickname,
			stampCount,
			user.isRewardNeeded()
		);
	}

	public Reward createReward(User user, RewardRequest rewardRequest) {
		return Reward.builder()
			.user(user)
			.rewardImageUrl(rewardRequest.rewardImageUrl())
			.build();
	}

	public RewardGrantedEvent createRewardGrantedEvent(Long userId, String rewardName) {
		return RewardGrantedEvent.builder()
			.userId(userId)
			.rewardName(rewardName)
			.build();
	}
}
