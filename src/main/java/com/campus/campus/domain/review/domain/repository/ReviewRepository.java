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
import com.campus.campus.domain.user.domain.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	// 최신순
	@Query("""
			SELECT r
			FROM Review r
			WHERE r.place.placeId = :placeId
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
		@Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);

	// 평점순
	@Query("""
			SELECT r
			FROM Review r
			WHERE r.place.placeId = :placeId
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
			GROUP BY r.place.placeId
		""")
	List<PlaceStarAvgRow> findAverageStarsByPlaceIds(
		@Param("placeIds") Set<Long> placeIds
	);

	@Query("SELECT AVG(r.star) FROM Review r WHERE r.place.placeId = :placeId")
	Optional<Double> findAverageStarByPlaceId(@Param("placeId") Long placeId);

	@Query("""
		    SELECT r.place.placeKey, AVG(r.star)
		    FROM Review r
		    WHERE r.place.placeKey IN :placeKeys
		    GROUP BY r.place.placeKey
		""")
	List<Object[]> findAverageStarsByPlaceKeys(@Param("placeKeys") List<String> placeKeys);

	long countByPlace_PlaceId(long placeId);

	long countByPlaceAndUser(Place place, User user);

	long countByPlace_PlaceIdAndUser_Major_MajorId(long placeId, long majorId);

	long countByPlace_PlaceIdAndUser_College_CollegeId(Long placeId, long collegeId);

	long countByPlace_PlaceIdAndUser_School_SchoolId(Long placeId, long schoolId);

	List<Review> findTop3ByPlace_PlaceIdOrderByCreatedAtDesc(Long placeId);

	@Query(value = """
		SELECT r FROM Review r
		JOIN FETCH r.place
		WHERE r.user.id = :userId
		ORDER BY r.createdAt DESC
		""",
		countQuery = "SELECT count(r) FROM Review r WHERE r.user.id = :userId")
	Page<Review> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT r.place.placeId, COUNT(r) FROM Review r WHERE r.place.placeId IN :placeIds GROUP BY r.place.placeId")
	List<Object[]> findReviewCountsByPlaceIds(@Param("placeIds") Set<Long> placeIds);
}
