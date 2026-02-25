package com.campus.campus.domain.manager.application.dto.request;

import com.campus.campus.domain.inquiry.domain.entity.InquiryStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record InquirySearchCondition(
	@Schema(description = "작성자 타입(미입력 시 전체 조회)", allowableValues = {"USER", "STUDENT_COUNCIL"})
	String writerType,

	@Schema(description = "문의 상태(미입력 시 전체 조회)", example = "WAITING || COMPLETED")
	InquiryStatus status
) {
}
