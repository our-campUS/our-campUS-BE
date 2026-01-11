package com.campus.campus.domain.review.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.review.domain.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

	List<ReviewImage> findAllByReviewOrderbyIdAsc(Review review);
}
