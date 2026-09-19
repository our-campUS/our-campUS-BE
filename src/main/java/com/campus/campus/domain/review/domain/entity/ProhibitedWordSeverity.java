package com.campus.campus.domain.review.domain.entity;

public enum ProhibitedWordSeverity {
	BLOCK,  // 욕설, 성적 표현, 혐오/차별, 개인정보 → 등록 차단
	REVIEW, // 폭력/위협, 광고/스팸 → 등록은 되지만 검토 후 공개
}
