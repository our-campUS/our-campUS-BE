package com.campus.campus.domain.councilpost.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.councilpost.domain.entity.LikePost;

public interface  LikePostRepository extends JpaRepository<LikePost, Long> {
	Optional<LikePost> findByUserIdAndPost_Id(Long userId, Long postId);
}
