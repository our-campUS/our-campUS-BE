package com.campus.campus.domain.review.domain;

public enum ReviewModerationResult {
	PASS,         // 정상 → 바로 공개
	NEEDS_REVIEW, // 등록되지만 검토 상태(비공개)
	BLOCK,        // 등록 차단
}
