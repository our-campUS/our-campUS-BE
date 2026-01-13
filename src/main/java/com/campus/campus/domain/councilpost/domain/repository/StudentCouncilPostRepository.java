package com.campus.campus.domain.councilpost.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;

public interface StudentCouncilPostRepository extends JpaRepository<StudentCouncilPost, Long> {

	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN FETCH p.writer w
		JOIN FETCH w.school
		LEFT JOIN FETCH w.college
		LEFT JOIN FETCH w.major
		WHERE p.id = :postId
		AND w.deletedAt IS NULL
		""")
	Optional<StudentCouncilPost> findByIdWithFullInfo(@Param("postId") Long postId);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major"})
	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN p.writer w
		WHERE w.id = :councilId
		  AND p.category = :category
		  AND p.startDateTime BETWEEN :now AND :limit
		  AND w.deletedAt IS NULL
		ORDER BY p.startDateTime ASC
		""")
	Page<StudentCouncilPost> findUpcomingEventsByCouncil(
		@Param("councilId") Long councilId,
		@Param("category") PostCategory category,
		@Param("now") LocalDateTime now,
		@Param("limit") LocalDateTime limit,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major"})
	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN p.writer w
		WHERE w.id = :councilId
		  AND (:category IS NULL OR p.category = :category)
		  AND (
		    (p.category = com.campus.campus.domain.councilpost.domain.entity.PostCategory.EVENT
		      AND p.startDateTime >= :now)
		    OR (p.category = com.campus.campus.domain.councilpost.domain.entity.PostCategory.PARTNERSHIP
		      AND p.endDateTime >= :now)
		  )
		  AND w.deletedAt IS NULL
		""")
	Page<StudentCouncilPost> findPostsByCouncilAndFilters(@Param("councilId") Long councilId,
		@Param("category") PostCategory category,
		@Param("now") LocalDateTime now,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major"})
	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN p.writer w
		LEFT JOIN w.college c
		LEFT JOIN w.major m
		WHERE w.school.schoolId = :schoolId
		  AND w.councilType = :councilType
		  AND (:collegeId IS NULL OR c.collegeId = :collegeId)
		  AND (:majorId IS NULL OR m.majorId = :majorId)
		  AND p.category = :category
		  AND :now BETWEEN p.startDateTime AND p.endDateTime
		  AND w.deletedAt IS NULL
		ORDER BY function('RAND')
		""")
	List<StudentCouncilPost> findRandomActivePartnerships(
		@Param("schoolId") Long schoolId,
		@Param("councilType") CouncilType councilType,
		@Param("category") PostCategory category,
		@Param("collegeId") Long collegeId,
		@Param("majorId") Long majorId,
		@Param("now") LocalDateTime now,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major"})
	@Query("""
		
		SELECT p FROM StudentCouncilPost p
		      JOIN p.writer w
		      JOIN w.school s
		      WHERE w.councilType = :councilType
		        AND s.schoolId = :schoolId
		        AND (:category IS NULL OR p.category = :category)
				AND (:excludePostId IS NULL OR p.id <> :excludePostId)
		        AND w.deletedAt IS NULL
		""")
	Page<StudentCouncilPost> findBySchoolId(
		@Param("schoolId") Long schoolId,
		@Param("category") PostCategory category,
		@Param("councilType") CouncilType councilType,
		@Param("excludePostId") Long excludePostId,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major"})
	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN p.writer w
		JOIN w.college c
		WHERE w.councilType = :councilType
		  AND c.collegeId = :collegeId
		  AND (:category IS NULL OR p.category = :category)
		  AND (:excludePostId IS NULL OR p.id <> :excludePostId)
		  AND w.deletedAt IS NULL
		""")
	Page<StudentCouncilPost> findByCollegeId(
		@Param("collegeId") Long collegeId,
		@Param("category") PostCategory category,
		@Param("councilType") CouncilType councilType,
		@Param("excludePostId") Long excludePostId,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major"})
	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN p.writer w
		JOIN w.major m
		WHERE w.councilType = :councilType
		  AND m.majorId = :majorId
		  AND (:category IS NULL OR p.category = :category)
		  AND (:excludePostId IS NULL OR p.id <> :excludePostId)
		  AND w.deletedAt IS NULL
		""")
	Page<StudentCouncilPost> findByMajorId(
		@Param("majorId") Long majorId,
		@Param("category") PostCategory category,
		@Param("councilType") CouncilType councilType,
		@Param("excludePostId") Long excludePostId,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major"})
	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN p.writer w
		JOIN w.school s
		WHERE w.councilType = :councilType
		  AND s.schoolId = :schoolId
		  AND p.category = :category
		  AND p.startDateTime BETWEEN :now AND :limit
		  AND w.deletedAt IS NULL
		ORDER BY p.startDateTime ASC
		""")
	Page<StudentCouncilPost> findUpcomingSchoolEvents(
		@Param("schoolId") Long schoolId,
		@Param("category") PostCategory category,
		@Param("councilType") CouncilType councilType,
		@Param("now") LocalDateTime now,
		@Param("limit") LocalDateTime limit,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major"})
	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN p.writer w
		JOIN w.college c
		WHERE w.councilType = :councilType
		  AND c.collegeId = :collegeId
		  AND p.category = :category
		  AND p.startDateTime BETWEEN :now AND :limit
		  AND w.deletedAt IS NULL
		ORDER BY p.startDateTime ASC
		""")
	Page<StudentCouncilPost> findUpcomingCollegeEvents(
		@Param("collegeId") Long collegeId,
		@Param("category") PostCategory category,
		@Param("councilType") CouncilType councilType,
		@Param("now") LocalDateTime now,
		@Param("limit") LocalDateTime limit,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major"})
	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN p.writer w
		JOIN w.major m
		WHERE w.councilType = :councilType
		  AND m.majorId = :majorId
		  AND p.category = :category
		  AND p.startDateTime BETWEEN :now AND :limit
		  AND w.deletedAt IS NULL
		ORDER BY p.startDateTime ASC
		""")
	Page<StudentCouncilPost> findUpcomingMajorEvents(
		@Param("majorId") Long majorId,
		@Param("category") PostCategory category,
		@Param("councilType") CouncilType councilType,
		@Param("now") LocalDateTime now,
		@Param("limit") LocalDateTime limit,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"writer", "writer.school", "writer.college", "writer.major", "place"})
	@Query("""
			SELECT p
			FROM StudentCouncilPost p
			JOIN p.writer w
			LEFT JOIN w.school s
			LEFT JOIN w.college c
			LEFT JOIN w.major m
			JOIN p.place pl
			WHERE w.deletedAt IS NULL
			  AND p.category = :category
			  AND p.startDateTime <= :now
			  AND p.endDateTime >= :now
			  AND (
				   (w.councilType = :majorType AND m.majorId = :majorId)
				OR (w.councilType = :collegeType AND c.collegeId = :collegeId)
				OR (w.councilType = :schoolType AND s.schoolId = :schoolId)
			  )
			  AND (:cursor IS NULL OR p.id < :cursor)
			ORDER BY p.id DESC
		""")
	List<StudentCouncilPost> findByUserScopeWithCursor(
		@Param("majorId") Long majorId,
		@Param("collegeId") Long collegeId,
		@Param("schoolId") Long schoolId,
		@Param("category") PostCategory category,
		@Param("majorType") CouncilType majorType,
		@Param("collegeType") CouncilType collegeType,
		@Param("schoolType") CouncilType schoolType,
		@Param("cursor") Long cursor,
		@Param("now") LocalDateTime now,
		Pageable pageable
	);

	@Query("""
			SELECT p
			FROM StudentCouncilPost p
			JOIN p.writer w
			JOIN FETCH p.place pl
			LEFT JOIN w.school s
			LEFT JOIN w.college c
			LEFT JOIN w.major m
			WHERE w.deletedAt IS NULL
			  AND p.category = :category
			  AND p.startDateTime <= :now
			  AND p.endDateTime >= :now
			  AND pl.coordinate.latitude BETWEEN :minLat AND :maxLat
			  AND pl.coordinate.longitude BETWEEN :minLng AND :maxLng
			  AND (
				   (w.councilType = :majorType AND m.majorId = :majorId)
				OR (w.councilType = :collegeType AND c.collegeId = :collegeId)
				OR (w.councilType = :schoolType AND s.schoolId = :schoolId)
			  )
			ORDER BY p.id DESC
		""")
	List<StudentCouncilPost> findPinsInBounds(
		@Param("majorId") Long majorId,
		@Param("collegeId") Long collegeId,
		@Param("schoolId") Long schoolId,
		@Param("category") PostCategory category,
		@Param("majorType") CouncilType majorType,
		@Param("collegeType") CouncilType collegeType,
		@Param("schoolType") CouncilType schoolType,
		@Param("minLat") Double minLat,
		@Param("maxLat") Double maxLat,
		@Param("minLng") Double minLng,
		@Param("maxLng") Double maxLng,
		@Param("now") LocalDateTime now
	);

	@EntityGraph(attributePaths = {"places"})
	@Query(value = """
		SELECT scp.*
		       FROM student_council_post scp
		       JOIN student_councils sc
		         ON scp.writer_id = sc.student_council_id
		       JOIN places p
		         ON scp.place_id = p.place_id
		       LEFT JOIN reviews r
		         ON r.place_id = p.place_id
		         AND r.created_at >= :from
		       WHERE scp.start_date_time <= :now
		         AND scp.end_date_time >= :now
		         AND (
		               (sc.council_type = 'MAJOR_COUNCIL'   AND sc.major_id   = :majorId)
		            OR (sc.council_type = 'COLLEGE_COUNCIL' AND sc.college_id = :collegeId)
		            OR (sc.council_type = 'SCHOOL_COUNCIL'  AND sc.school_id  = :schoolId)
		         )
		       GROUP BY scp.id
		       ORDER BY COUNT(r.id) DESC
		       LIMIT 3;
		
		""", nativeQuery = true)
	List<StudentCouncilPost> findTop3RecommendedPartnershipPlaces(
		@Param("majorId") Long majorId,
		@Param("collegeId") Long collegeId,
		@Param("schoolId") Long schoolId,
		@Param("from") LocalDateTime from,
		@Param("now") LocalDateTime now
	);

}
