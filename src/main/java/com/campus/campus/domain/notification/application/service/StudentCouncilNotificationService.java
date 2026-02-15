package com.campus.campus.domain.notification.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.notification.application.dto.CursorResponse;
import com.campus.campus.domain.notification.application.dto.NextCursor;
import com.campus.campus.domain.notification.application.dto.NotificationResponse;
import com.campus.campus.domain.notification.application.exception.NotificationAccessDeniedException;
import com.campus.campus.domain.notification.application.exception.NotificationNotFoundException;
import com.campus.campus.domain.notification.application.mapper.NotificationMapper;
import com.campus.campus.domain.notification.domain.entity.Notification;
import com.campus.campus.domain.notification.domain.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentCouncilNotificationService {

	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;
	private final StudentCouncilRepository studentCouncilRepository;

	public CursorResponse<NotificationResponse> getCouncilNotificationsByCursor(
		Long councilId,
		LocalDateTime cursorCreatedAt,
		Long cursorId,
		int limit
	) {
		StudentCouncil council = studentCouncilRepository
			.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		int pageSize = Math.min(Math.max(limit, 1), 50);
		Pageable pageable = PageRequest.of(0, pageSize);

		boolean isFirst = (cursorCreatedAt == null || cursorId == null);

		List<Notification> list = isFirst
			? notificationRepository.findByStudentCouncilOrderByCreatedAtDescIdDesc(council, pageable)
			: notificationRepository.findNextByCouncilCursor(council, cursorCreatedAt, cursorId, pageable);

		List<NotificationResponse> items = list.stream()
			.map(notificationMapper::toResponse)
			.toList();

		boolean hasNext = list.size() == pageSize;
		NextCursor nextCursor = list.isEmpty() ? null :
			new NextCursor(list.get(list.size() - 1).getCreatedAt(), list.get(list.size() - 1).getId());

		return new CursorResponse<>(items, nextCursor, hasNext);
	}

	@Transactional
	public void markCouncilNotificationAsRead(Long councilId, Long notificationId) {
		StudentCouncil council = studentCouncilRepository
			.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(NotificationNotFoundException::new);

		if (notification.getStudentCouncil() == null ||
			!notification.getStudentCouncil().getId().equals(council.getId())) {
			throw new NotificationAccessDeniedException();
		}

		notification.markAsRead();
	}
}
