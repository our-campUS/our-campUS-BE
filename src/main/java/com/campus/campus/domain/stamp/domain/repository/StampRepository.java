package com.campus.campus.domain.stamp.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.stamp.domain.entity.Stamp;
import com.campus.campus.domain.user.domain.entity.User;

public interface StampRepository extends JpaRepository<Stamp, Long> {
	boolean existsByReview(Review review);

	int countByUser(User user);

	@Query("""
		SELECT s
		FROM Stamp s
		JOIN FETCH s.review r
		JOIN FETCH r.place
		WHERE s.user = :user
		ORDER BY s.createdAt DESC
		""")
	List<Stamp> findAllByUserWithReviewAndPlace(@Param("user") User user);
}
