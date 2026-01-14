package com.campus.campus.domain.review.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.review.application.dto.response.PlaceStarAvgRow;
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

	@Query("""
			SELECT new com.campus.campus.domain.review.application.dto.response.PlaceStarAvgRow(
				r.place.placeId,
				AVG(r.star)
			)
			FROM Review r
			WHERE r.place.placeId IN :placeIds
			GROUP BY r.place.placeId
		""")
	List<PlaceStarAvgRow> findAverageStarsByPlaceIds(
		@Param("placeIds") Set<Long> placeIds
	);

	long countByPlace_PlaceId(long placeId);

	long countByPlace_PlaceIdAndUser_Id(long placeId, long userId);

	long countByPlace_PlaceIdAndUser_Major_MajorId(long placeId, long majorId);

	long countByPlace_PlaceIdAndUser_College_CollegeId(Long placeId, long collegeId);

	long countByPlace_PlaceIdAndUser_School_SchoolId(Long placeId, long schoolId);

	List<Review> findAllByPlaceId(long placeId);

	List<Review> findTop3ByPlace_PlaceIdOrderByCreatedAtDesc(Long placeId);
}
