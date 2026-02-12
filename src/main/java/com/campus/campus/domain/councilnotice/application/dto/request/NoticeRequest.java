package com.campus.campus.domain.councilnotice.application.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record NoticeRequest(
	@Schema(description = "공지 이름", example = "중간고사")
	String title,

	@Schema(description = "공지 내용", example = "중간고사를 진행합니다.")
	String content,

	@Schema(description = "공지 image urls")
	List<String> imageUrls
) {
}
