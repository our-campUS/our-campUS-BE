package com.campus.campus.domain.notification.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.councilpost.application.dto.request.CouncilPostCreatedEvent;
import com.campus.campus.domain.notification.application.dto.CursorResponse;
import com.campus.campus.domain.notification.application.dto.NextCursor;
import com.campus.campus.domain.notification.application.dto.NotificationResponse;
import com.campus.campus.domain.notification.application.exception.NotificationAccessDeniedException;
import com.campus.campus.domain.notification.application.exception.NotificationNotFoundException;
import com.campus.campus.domain.notification.application.mapper.NotificationMapper;
import com.campus.campus.domain.notification.domain.entity.Notification;
import com.campus.campus.domain.notification.domain.entity.NotificationType;
import com.campus.campus.domain.notification.domain.repository.NotificationRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;
	private final UserRepository userRepository;

	public CursorResponse<NotificationResponse> getNotificationsByCursor(
		Long userId,
		LocalDateTime cursorCreatedAt,
		Long cursorId,
		int limit
	) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		int pageSize = Math.min(Math.max(limit, 1), 50);
		Pageable pageable = PageRequest.of(0, pageSize);

		boolean isFirst = (cursorCreatedAt == null || cursorId == null);

		List<Notification> list = isFirst
			? notificationRepository.findByUserOrderByCreatedAtDescIdDesc(user, pageable)
			: notificationRepository.findNextByCursor(user, cursorCreatedAt, cursorId, pageable);

		List<NotificationResponse> items = list.stream()
			.map(notificationMapper::toResponse)
			.toList();

		boolean hasNext = list.size() == pageSize;

		NextCursor nextCursor = null;
		if (!list.isEmpty()) {
			Notification last = list.get(list.size() - 1);
			nextCursor = new NextCursor(last.getCreatedAt(), last.getId());
		}

		return new CursorResponse<>(items, nextCursor, hasNext);
	}

	@Transactional
	public void markAsRead(Long userId, Long notificationId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(NotificationNotFoundException::new);

		if (!notification.getUser().getId().equals(user.getId())) {
			throw new NotificationAccessDeniedException();
		}

		notification.markAsRead();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveCouncilPostCreated(CouncilPostCreatedEvent event, String title, String body) {

		List<User> targetUsers = findUsersByTopic(event.topic());

		List<Notification> notifications = targetUsers.stream()
			.map(user -> notificationMapper.createNotification(
				user,
				NotificationType.COUNCIL_POST_CREATED,
				title,
				body,
				event.postId()
			))
			.toList();

		notificationRepository.saveAll(notifications);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveRewardGrantedNotification(Long userId, String title, String body) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		Notification notification = notificationMapper.createNotification(user, NotificationType.REWARD_GRANTED, title,
			body, null);

		notificationRepository.save(notification);
	}

	@Transactional(readOnly = true)
	public boolean hasUnread(Long userId) {
		return notificationRepository.existsByUser_IdAndIsReadFalse(userId);
	}

	private List<User> findUsersByTopic(String topic) {

		String[] parts = topic.split("_");
		String scope = parts[0];
		Long scopeId = Long.valueOf(parts[1]);

		return switch (scope) {
			case "major" -> userRepository.findAllByMajor_MajorIdAndDeletedAtIsNull(scopeId);
			case "college" -> userRepository.findAllByCollege_CollegeIdAndDeletedAtIsNull(scopeId);
			case "school" -> userRepository.findAllBySchool_SchoolIdAndDeletedAtIsNull(scopeId);
			default -> List.of();
		};
	}
}
