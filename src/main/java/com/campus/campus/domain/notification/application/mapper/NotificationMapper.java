package com.campus.campus.domain.notification.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.notification.application.dto.NotificationResponse;
import com.campus.campus.domain.notification.domain.entity.Notification;
import com.campus.campus.domain.notification.domain.entity.NotificationType;
import com.campus.campus.domain.notification.util.TimeFormatter;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

	private final TimeFormatter timeFormatter;

	public NotificationResponse toResponse(Notification notification) {
		return new NotificationResponse(
			notification.getId(),
			notification.getType(),
			notification.getTitle(),
			notification.getBody(),
			notification.getReferenceId(),
			notification.isRead(),
			timeFormatter.formatRelativeTime(notification.getCreatedAt())
		);
	}

	public Notification createNotification(User user, NotificationType type,
		String title, String body, Long referenceId) {
		return Notification.builder()
			.user(user)
			.type(type)
			.title(title)
			.body(body)
			.referenceId(referenceId)
			.build();
	}

	public Notification createCouncilNotification(StudentCouncil council, NotificationType type,
		String title, String body, Long referenceId) {
		return Notification.builder()
			.studentCouncil(council)
			.type(type)
			.title(title)
			.body(body)
			.referenceId(referenceId)
			.build();
	}
}
