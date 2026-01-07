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

	@Query("SELECT p FROM StudentCouncilPost p " +
		"JOIN FETCH p.writer w " +
		"JOIN FETCH w.school s " +
		"LEFT JOIN FETCH w.college c " +
		"LEFT JOIN FETCH w.major m " +
		"WHERE p.id = :postId")
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
		WHERE w.school.schoolId = :schoolId
		  AND (:councilType IS NULL OR w.councilType = :councilType)
		  AND (:category IS NULL OR p.category = :category)
		  AND w.deletedAt IS NULL
		""")
	Page<StudentCouncilPost> findPostsBySchoolAndFilters(@Param("schoolId") Long schoolId,
		@Param("councilType") CouncilType councilType, @Param("category") PostCategory category, Pageable pageable
	);
}
