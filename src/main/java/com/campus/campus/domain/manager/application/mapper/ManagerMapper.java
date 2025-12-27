package com.campus.campus.domain.manager.application.mapper;

import org.springframework.stereotype.Component;

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
}
