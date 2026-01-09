package com.campus.campus.domain.partnership.domain.entity;

public enum PartnershipStatus {

	ACTIVE,     // 현재 제휴 중
	EXPIRED,    // 제휴 기간 만료
	SUSPENDED;  // 일시 중단 (관리자/정책에 의함)

	/**
	 * 사용자에게 노출 가능한 상태인지 여부
	 */
	public boolean isVisible() {
		return this == ACTIVE;
	}

	/**
	 * 혜택 제공이 가능한 상태인지 여부
	 */
	public boolean isBenefitAvailable() {
		return this == ACTIVE;
	}
}

