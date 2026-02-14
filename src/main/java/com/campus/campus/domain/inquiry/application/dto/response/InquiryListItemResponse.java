package com.campus.campus.domain.inquiry.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record InquiryListItemResponse(
	@Schema(description = "문의 ID", example = "1")
	Long id,

	@Schema(description = "작성자 ID", example = "2")
	Long writerId,

	@Schema(description = "작성자 이름", example = "홍길동")
	String writerName,

	@Schema(description = "작성자 타입", example = "USER || STUDENT_COUNCIL")
	String writerType,

	@Schema(description = "문의 제목", example = "서비스 문의 드려요")
	String title,

	@Schema(description = "문의 내용", example = "문의 남겨요.")
	String content,

	@Schema(description = "문의 상태", example = "WAITING")
	String status,

	@Schema(description = "문의 답변", example = "안녕하세요, 캠어스입니다.")
	String answer,

	@Schema(description = "문의 생성일", example = "2026-02-02T23:59:59")
	LocalDateTime createdAt,

	@Schema(description = "문의 답변일", example = "2026-02-02T23:59:59")
	LocalDateTime answeredAt
) {
}
