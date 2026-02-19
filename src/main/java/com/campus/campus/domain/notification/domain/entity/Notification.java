package com.campus.campus.domain.notification.domain.entity;

import java.time.LocalDateTime;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_council_id")
	private StudentCouncil studentCouncil;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType type;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String body;

	@Column(name = "reference_id")
	private Long referenceId; // postId, commentId 등

	@Column(nullable = false)
	private boolean isRead = false;

	@Column(name = "read_at")
	private LocalDateTime readAt;

	@Builder
	public Notification(User user, StudentCouncil studentCouncil, NotificationType type, String title,
		String body, Long referenceId) {
		this.user = user;
		this.studentCouncil = studentCouncil;
		this.type = type;
		this.title = title;
		this.body = body;
		this.referenceId = referenceId;
	}

	public void markAsRead() {
		this.isRead = true;
		this.readAt = LocalDateTime.now();
	}
}
