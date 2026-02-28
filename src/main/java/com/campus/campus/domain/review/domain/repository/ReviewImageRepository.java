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
		SELECT ri.review.place.placeId, ri.imageUrl
		FROM ReviewImage ri
		WHERE ri.review.place.placeId IN :placeIds
		  AND ri.id = (
		    SELECT MIN(ri2.id)
		    FROM ReviewImage ri2
		    WHERE ri2.review.place.placeId = ri.review.place.placeId
		  )
		""")
	List<Object[]> findOldestImageUrlsByPlaceIds(@Param("placeIds") Set<Long> placeIds);

}
