package com.campus.campus.domain.councilpost.domain.repository;

import java.time.LocalDateTime;
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

	Page<StudentCouncilPost> findAllByCategory(PostCategory category, Pageable pageable);

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

	@Query("""
		SELECT p
		FROM StudentCouncilPost p
		WHERE p.category = :category
		AND p.startDateTime BETWEEN :now AND :limit
		""")
	Page<StudentCouncilPost> findUpcomingEvents(
		@Param("category") PostCategory category,
		@Param("now") LocalDateTime now,
		@Param("limit") LocalDateTime limit,
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
		        AND w.deletedAt IS NULL
		""")
	Page<StudentCouncilPost> findBySchoolId(
		@Param("schoolId") Long schoolId,
		@Param("category") PostCategory category,
		@Param("councilType") CouncilType councilType,
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
		  AND w.deletedAt IS NULL
		""")
	Page<StudentCouncilPost> findByCollegeId(
		@Param("collegeId") Long collegeId,
		@Param("category") PostCategory category,
		@Param("councilType") CouncilType councilType,
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
		  AND w.deletedAt IS NULL
		""")
	Page<StudentCouncilPost> findByMajorId(
		@Param("majorId") Long majorId,
		@Param("category") PostCategory category,
		@Param("councilType") CouncilType councilType,
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
}
