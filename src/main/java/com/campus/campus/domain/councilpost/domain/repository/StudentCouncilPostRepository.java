package com.campus.campus.domain.councilpost.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN FETCH p.writer w
		JOIN FETCH w.school
		WHERE w.councilType = 'SCHOOL_COUNCIL'
		AND w.school.schoolId = :schoolId
		AND (:category IS NULL OR p.category = :category)
		AND w.deletedAt IS NULL
		ORDER BY p.createdAt DESC
		""")
	Page<StudentCouncilPost> findBySchoolId(
		@Param("schoolId") Long schoolId,
		@Param("category") PostCategory category,
		Pageable pageable
	);

	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN FETCH p.writer w
		JOIN FETCH w.college
		WHERE w.councilType = 'COLLEGE_COUNCIL'
		AND w.college.collegeId = :collegeId
		AND (:category IS NULL OR p.category = :category)
		AND w.deletedAt IS NULL
		ORDER BY p.createdAt DESC
		""")
	Page<StudentCouncilPost> findByCollegeId(
		@Param("collegeId") Long collegeId,
		@Param("category") PostCategory category,
		Pageable pageable
	);

	@Query("""
		SELECT p FROM StudentCouncilPost p
		JOIN FETCH p.writer w
		JOIN FETCH w.major
		WHERE w.councilType = 'MAJOR_COUNCIL'
		AND w.major.majorId = :majorId
		AND (:category IS NULL OR p.category = :category)
		AND w.deletedAt IS NULL
		ORDER BY p.createdAt DESC
		""")
	Page<StudentCouncilPost> findByMajorId(
		@Param("majorId") Long majorId,
		@Param("category") PostCategory category,
		Pageable pageable
	);
}
