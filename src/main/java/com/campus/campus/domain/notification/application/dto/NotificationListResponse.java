package com.campus.campus.domain.notification.application.dto;

import java.util.List;

public record NotificationListResponse(
	List<NotificationResponse> notifications,
	boolean hasNext
) {
}
