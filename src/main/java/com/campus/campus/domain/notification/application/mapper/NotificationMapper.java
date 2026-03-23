package com.campus.campus.domain.notification.application.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.notification.application.dto.NotificationResponse;
import com.campus.campus.domain.notification.domain.entity.Notification;
import com.campus.campus.domain.notification.domain.entity.NotificationType;
import com.campus.campus.domain.notification.domain.entity.SenderType;
import com.campus.campus.domain.notification.util.TimeFormatter;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

	private final TimeFormatter timeFormatter;

	public NotificationResponse toResponse(Notification notification) {

		String senderProfileImage;
		SenderType senderType;

		if (notification.getStudentCouncil() != null) {
			senderType = SenderType.STUDENT_COUNCIL;
			senderProfileImage = notification.getStudentCouncil().getCouncilProfileImageUrl();
		} else {
			senderType = SenderType.SYSTEM;
			senderProfileImage = null;
		}

		return new NotificationResponse(
			notification.getId(),
			notification.getType(),
			notification.getTitle(),
			notification.getBody(),
			senderType,
			senderProfileImage,
			notification.getReferenceId(),
			notification.isRead(),
			timeFormatter.formatRelativeTime(notification.getCreatedAt())
		);
	}

	public Notification createNotification(User user, StudentCouncil studentCouncil, NotificationType type,
		String title, String body, Long referenceId) {
		return Notification.builder()
			.user(user)
			.studentCouncil(studentCouncil)
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
