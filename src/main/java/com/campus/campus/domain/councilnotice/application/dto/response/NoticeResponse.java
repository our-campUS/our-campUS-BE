package com.campus.campus.domain.councilnotice.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record NoticeResponse(
	@Schema(description = "공지 id", example = "1")
	Long id,

	@Schema(description = "작성자 id", example = "1")
	Long writerId,

	@Schema(description = "작성자 이름", example = "중앙대학교 총학생회")
	String writerName,

	@Schema(description = "작성자 여부", example = "true")
	boolean isWriter,

	@Schema(description = "공지 이름", example = "중간고사")
	String title,

	@Schema(description = "공지 내용", example = "중간고사 진행합니다.")
	String content,

	@Schema(description = "공지 image urls")
	List<String> images,

	@Schema(description = "공지 생성 시간", example = "2026-01-10T18:00:00")
	LocalDateTime createdAt,

	@Schema(description = "공지 업데이트 시간", example = "2026-01-10T18:00:00")
	LocalDateTime updatedAt
) {
}
