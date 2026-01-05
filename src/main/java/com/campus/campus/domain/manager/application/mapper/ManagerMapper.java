package com.campus.campus.domain.manager.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.manager.application.dto.response.CertifyRequestCouncilResponse;
import com.campus.campus.domain.manager.application.dto.response.CouncilApproveOrDenyResponse;
import com.campus.campus.domain.manager.application.dto.response.CertifyRequestCouncilListResponse;
import com.campus.campus.domain.manager.application.dto.response.ManagerLoginResponse;
import com.campus.campus.domain.manager.domain.entity.Manager;

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

	public CouncilApproveOrDenyResponse toCouncilApproveOrDenyResponse(Long councilId, boolean certifyResult) {
		return new CouncilApproveOrDenyResponse(
			councilId,
			certifyResult
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
}
