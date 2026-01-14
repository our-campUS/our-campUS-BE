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
import com.campus.campus.domain.notification.application.service.NotificationService;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "알림", description = "알림 관련 API")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	@Operation(
		summary = "알림 목록 조회",
		description = "사용자의 알림 목록을 최신순으로 조회합니다."
	)
	public CommonResponse<CursorResponse<NotificationResponse>> getNotifications(
		@RequestParam(defaultValue = "20") int limit,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		LocalDateTime cursorCreatedAt,
		@RequestParam(required = false) Long cursorId,
		@CurrentUserId Long userId
	) {
		CursorResponse<NotificationResponse> response =
			notificationService.getNotificationsByCursor(userId, cursorCreatedAt, cursorId, limit);

		return CommonResponse.success(NotificationResponseCode.NOTIFICATION_LIST_READ_SUCCESS, response);
	}

	@PatchMapping("/{notificationId}/read")
	@Operation(
		summary = "특정 알림 읽음 처리",
		description = "특정 알림을 읽음 상태로 변경합니다."
	)
	public CommonResponse<Void> markAsRead(
		@PathVariable Long notificationId,
		@CurrentUserId Long userId
	) {
		notificationService.markAsRead(userId, notificationId);
		return CommonResponse.success(NotificationResponseCode.NOTIFICATION_READ_SUCCESS);
	}

	@GetMapping("/unread/exists")
	@Operation(
		summary = "미확인 알림 여부 확인",
		description = "홈에서 알림 아이콘에 빨간 점을 표시하기 위해 미확인 알림이 있는지 확인합니다."
	)
	public CommonResponse<Boolean> hasUnread(@CurrentUserId Long userId) {

		boolean hasUnread = notificationService.hasUnread(userId);

		return CommonResponse.success(
			NotificationResponseCode.NOTIFICATION_UNREAD_EXISTS_SUCCESS,
			hasUnread
		);
	}
}