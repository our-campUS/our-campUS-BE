package com.campus.campus.domain.review.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.review.domain.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
