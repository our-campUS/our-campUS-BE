package com.campus.campus.domain.notification.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.notification.domain.entity.Notification;
import com.campus.campus.domain.user.domain.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserOrderByCreatedAtDescIdDesc(User user, Pageable pageable);

	@Query("""
			select n from Notification n
			where n.user = :user
			  and (
			       n.createdAt < :cursorCreatedAt
			    or (n.createdAt = :cursorCreatedAt and n.id < :cursorId)
			  )
			order by n.createdAt desc, n.id desc
		""")
	List<Notification> findNextByCursor(
		@Param("user") User user,
		@Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);

	boolean existsByUser_IdAndIsReadFalse(Long userId);

	List<Notification> findByStudentCouncilOrderByCreatedAtDescIdDesc(StudentCouncil council, Pageable pageable);

	@Query("""
			select n from Notification n
			where n.studentCouncil = :council
			  and (
			       n.createdAt < :cursorCreatedAt
			    or (n.createdAt = :cursorCreatedAt and n.id < :cursorId)
			  )
			order by n.createdAt desc, n.id desc
		""")
	List<Notification> findNextByCouncilCursor(
		@Param("council") StudentCouncil council,
		@Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);
}
