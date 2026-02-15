package com.campus.campus.domain.notification.presentation;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.notification.application.dto.CursorResponse;
import com.campus.campus.domain.notification.application.dto.NotificationResponse;
import com.campus.campus.domain.notification.application.service.StudentCouncilNotificationService;
import com.campus.campus.global.annotation.CurrentCouncilId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/student-councils/notifications")
@RequiredArgsConstructor
@Tag(name = "학생회 알림", description = "학생회 전용 알림 관련 API")
public class StudentCouncilNotificationController {

	private final StudentCouncilNotificationService councilNotificationService;

	@GetMapping
	@Operation(
		summary = "학생회 알림 목록 조회",
		description = "학생회 계정의 알림 목록을 최신순으로 조회합니다. (커서 기반 페이징)"
	)
	public CommonResponse<CursorResponse<NotificationResponse>> getCouncilNotifications(
		@RequestParam(defaultValue = "20") int limit,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		LocalDateTime cursorCreatedAt,
		@RequestParam(required = false) Long cursorId,
		@CurrentCouncilId Long councilId
	) {
		CursorResponse<NotificationResponse> response =
			councilNotificationService.getCouncilNotificationsByCursor(councilId, cursorCreatedAt, cursorId, limit);

		return CommonResponse.success(NotificationResponseCode.NOTIFICATION_READ_SUCCESS, response);
	}

	@PatchMapping("/{notificationId}/read")
	@Operation(
		summary = "학생회 특정 알림 읽음 처리",
		description = "학생회 알림을 읽음 상태로 변경합니다."
	)
	public CommonResponse<Void> markAsRead(
		@PathVariable Long notificationId,
		@CurrentCouncilId Long councilId
	) {
		councilNotificationService.markCouncilNotificationAsRead(councilId, notificationId);
		return CommonResponse.success(NotificationResponseCode.NOTIFICATION_READ_SUCCESS);
	}
}
