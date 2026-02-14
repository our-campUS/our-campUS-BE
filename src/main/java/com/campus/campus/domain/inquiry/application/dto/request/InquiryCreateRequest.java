package com.campus.campus.domain.inquiry.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InquiryCreateRequest(
	@NotBlank(message = "제목을 입력해주세요.")
	@Size(max = 100)
	String title,

	@NotBlank(message = "내용을 입력해주세요.")
	@Size(min = 10, message = "내용은 최소 10자 이상 작성해주세요.")
	@Size(max = 1000)
	String content
) {
}
