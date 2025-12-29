package com.campus.campus.domain.councilpost.domain.repository;

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

	@Query("SELECT p FROM StudentCouncilPost p " +
		"JOIN FETCH p.writer w " +
		"JOIN FETCH w.school s " +
		"LEFT JOIN FETCH w.college c " +
		"LEFT JOIN FETCH w.major m " +
		"WHERE p.id = :postId")
	Optional<StudentCouncilPost> findByIdWithFullInfo(@Param("postId") Long postId);

	@Query("SELECT p FROM StudentCouncilPost p " +
		"JOIN FETCH p.writer w " +
		"LEFT JOIN FETCH w.school " +
		"LEFT JOIN FETCH w.college " +
		"LEFT JOIN FETCH w.major " +
		"WHERE p.id = :postId")
	Optional<StudentCouncilPost> findByIdWithWriter(@Param("postId") Long postId);

}
