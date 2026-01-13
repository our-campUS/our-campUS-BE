package com.campus.campus.domain.stamp.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.stamp.domain.entity.Stamp;
import com.campus.campus.domain.user.domain.entity.User;

public interface StampRepository extends JpaRepository<Stamp, Long> {
	boolean existsByReview(Review review);

	int countByUser(User user);
}
