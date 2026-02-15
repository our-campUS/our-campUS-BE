package com.campus.campus.domain.manager.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record InquiryAnswerRequest(
	@Schema(description = "답변 내용", example = "문의하신 내용에 대한 답변입니다.")
	@NotBlank(message = "답변 내용은 비어있을 수 없습니다.")
	String answer
) {
}
