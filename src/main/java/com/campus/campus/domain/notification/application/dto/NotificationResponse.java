package com.campus.campus.domain.notification.application.dto;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.notification.domain.entity.NotificationType;
import com.campus.campus.domain.notification.domain.entity.SenderType;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationResponse(
	@Schema(description = "알림 ID", example = "1")
	Long id,

	@Schema(description = "알림 타입", example = "COUNCIL_POST_CREATED")
	NotificationType type,

	@Schema(description = "알림 제목", example = "총학생회")
	String title,

	@Schema(description = "알림 내용", example = "새 행사글이 등록되었습니다.")
	String body,

	@Schema(description = "보낸 주체 타입", example = "STUDENT_COUNCIL")
	SenderType senderType,

	@Schema(description = "학생회 타입", example = "MAJOR_COUNCIL")
	CouncilType councilType,

	@Schema(description = "보낸 주체 프로필 이미지 URL", example = "https://cdn.example.com/council/profile.png")
	String profileImageUrl,

	@Schema(description = "참조 ID (게시글 ID 등)", example = "123")
	Long referenceId,

	@Schema(description = "읽음 여부", example = "false")
	boolean isRead,

	@Schema(description = "생성 시각 (상대 시간)", example = "5분 전")
	String createTimeBeforeNow
) {
}
