package com.campus.campus.domain.councilpost.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.councilpost.domain.entity.LikePost;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;

public interface LikePostRepository extends JpaRepository<LikePost, Long> {
	Optional<LikePost> findByUserIdAndPost_Id(Long userId, Long postId);

	@EntityGraph(attributePaths = {"post", "post.writer"})
	@Query("""
		SELECT lp FROM LikePost lp
		JOIN lp.post p
		WHERE lp.user.id = :userId
		  AND (:category IS NULL OR p.category = :category)
		ORDER BY lp.createdAt DESC
		""")
	Page<LikePost> findLikedPosts(@Param("userId") Long userId, @Param("category") PostCategory category,
		Pageable pageable);
}
