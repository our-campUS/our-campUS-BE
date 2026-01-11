package com.campus.campus.domain.review.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.review.domain.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

	List<ReviewImage> findAllByReviewOrderByIdAsc(Review review);

	List<ReviewImage> findAllByReviewIdInOrderByIdAsc(@Param("reviewIds") List<Long> reviewIds);

	List<ReviewImage> findAllByReview(Review review);

	void deleteByReview(Review review);

}
