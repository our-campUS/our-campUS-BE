package com.campus.campus.domain.review.application.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CursorPageReviewResponse<ReviewResponse> {

	private List<ReviewResponse> items;
	private String nextCursorCreatedAt;
	private Long nextCursorId;
	private boolean hasNext;
}
