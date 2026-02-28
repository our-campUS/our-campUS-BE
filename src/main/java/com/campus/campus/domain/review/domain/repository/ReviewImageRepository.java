package com.campus.campus.domain.review.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.review.domain.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

	List<ReviewImage> findAllByReviewOrderByIdAsc(Review review);

	List<ReviewImage> findAllByReviewIdInOrderByIdAsc(@Param("reviewIds") List<Long> reviewIds);

	List<ReviewImage> findAllByReview(Review review);

	void deleteByReview(Review review);

	@Query("""
		SELECT ri.review.place.placeId, MIN(ri.imageUrl)
		FROM ReviewImage ri
		WHERE ri.review.place.placeId IN :placeIds
		GROUP BY ri.review.place.placeId
		""")
	List<Object[]> findFirstImageUrlsByPlaceIds(@Param("placeIds") Set<Long> placeIds);

}
