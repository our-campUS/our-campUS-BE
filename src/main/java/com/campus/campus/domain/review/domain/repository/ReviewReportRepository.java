package com.campus.campus.domain.review.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.review.domain.entity.ReviewReport;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
	boolean existsByReporter_IdAndReviewId(Long reporterId, Long reviewId);
}
