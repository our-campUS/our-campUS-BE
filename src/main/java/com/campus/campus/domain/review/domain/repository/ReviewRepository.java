package com.campus.campus.domain.review.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.review.domain.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("""
			SELECT r
			FROM Review r
			WHERE r.place.placeId = :placeId
			  AND (
			    :cursorCreatedAt IS NULL
			    OR r.createdAt < :cursorCreatedAt
			    OR (r.createdAt = :cursorCreatedAt AND r.id < :cursorId)
			  )
			ORDER BY r.createdAt DESC, r.id DESC
		""")
	List<Review> findByPlaceIdWithCursor(
		@Param("placeId") Long placeId,
		@Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);
}
