package com.campus.campus.domain.review.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.review.application.dto.response.PlaceStarAvgRow;
import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.review.domain.entity.ReviewStatus;
import com.campus.campus.domain.user.domain.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	// 최신순 (공개 리뷰만)
	@Query("""
		SELECT r
		FROM Review r
		WHERE r.place.placeId = :placeId
		  AND r.status = :status
		  AND (
		    :cursorCreatedAt IS NULL
		    OR :cursorId IS NULL
		    OR r.createdAt < :cursorCreatedAt
		    OR (r.createdAt = :cursorCreatedAt AND r.id < :cursorId)
		  )
		ORDER BY r.createdAt DESC, r.id DESC
		""")
	List<Review> findByPlaceIdWithLatestCursor(
		@Param("placeId") Long placeId,
		@Param("status") ReviewStatus status,
		@Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);

	// 평점순 (공개 리뷰만)
	@Query("""
		SELECT r
		FROM Review r
		WHERE r.place.placeId = :placeId
		  AND r.status = :status
		  AND (
		    :cursorStar IS NULL
		    OR :cursorCreatedAt IS NULL
		    OR :cursorId IS NULL
		    OR r.star < :cursorStar
		    OR (r.star = :cursorStar AND r.createdAt < :cursorCreatedAt)
		    OR (r.star = :cursorStar AND r.createdAt = :cursorCreatedAt AND r.id < :cursorId)
		  )
		ORDER BY r.star DESC, r.createdAt DESC, r.id DESC
		""")
	List<Review> findByPlaceIdWithStarCursor(
		@Param("placeId") Long placeId,
		@Param("status") ReviewStatus status,
		@Param("cursorStar") Integer cursorStar,
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
		  AND r.status = :status
		GROUP BY r.place.placeId
		""")
	List<PlaceStarAvgRow> findAverageStarsByPlaceIds(
		@Param("placeIds") Set<Long> placeIds,
		@Param("status") ReviewStatus status
	);

	@Query("SELECT AVG(r.star) FROM Review r WHERE r.place.placeId = :placeId AND r.status = :status")
	Optional<Double> findAverageStarByPlaceId(
		@Param("placeId") Long placeId,
		@Param("status") ReviewStatus status
	);

	@Query("""
		SELECT r.place.placeKey, AVG(r.star)
		FROM Review r
		WHERE r.place.placeKey IN :placeKeys
		  AND r.status = :status
		GROUP BY r.place.placeKey
		""")
	List<Object[]> findAverageStarsByPlaceKeys(
		@Param("placeKeys") List<String> placeKeys,
		@Param("status") ReviewStatus status
	);

	long countByPlace_PlaceIdAndStatus(long placeId, ReviewStatus status);

	long countByPlaceAndUserAndStatus(Place place, User user, ReviewStatus status);

	long countByPlace_PlaceIdAndUser_Major_MajorIdAndStatus(long placeId, long majorId, ReviewStatus status);

	long countByPlace_PlaceIdAndUser_College_CollegeIdAndStatus(Long placeId, long collegeId, ReviewStatus status);

	long countByPlace_PlaceIdAndUser_School_SchoolIdAndStatus(Long placeId, long schoolId, ReviewStatus status);

	List<Review> findTop3ByPlace_PlaceIdAndStatusOrderByCreatedAtDesc(Long placeId, ReviewStatus status);

	@Query(value = """
		SELECT r FROM Review r
		JOIN FETCH r.place
		WHERE r.user.id = :userId
		ORDER BY r.createdAt DESC
		""",
		countQuery = "SELECT count(r) FROM Review r WHERE r.user.id = :userId")
	Page<Review> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

	@Query("""
		SELECT r.place.placeId, COUNT(r)
		FROM Review r
		WHERE r.place.placeId IN :placeIds
		  AND r.status = :status
		GROUP BY r.place.placeId
		""")
	List<Object[]> findReviewCountsByPlaceIds(
		@Param("placeIds") Set<Long> placeIds,
		@Param("status") ReviewStatus status
	);
}