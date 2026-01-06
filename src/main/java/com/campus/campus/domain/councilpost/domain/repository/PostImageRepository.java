package com.campus.campus.domain.councilpost.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.place.application.dto.response.partnership.PostImageSummary;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	List<PostImage> findAllByPost(StudentCouncilPost post);

	void deleteByPost(StudentCouncilPost post);

	List<PostImage> findAllByPostOrderByIdAsc(StudentCouncilPost post);

	@Query("""
		SELECT NEW com.campus.campus.domain.place.application.dto.response.partnership.PostImageSummary
		(post.place.placeId,
		img.imageUrl)
		FROM PostImage img
		JOIN img.post post
		WHERE post.category='PARTNERSHIP' 
				AND post.place.placeId in :placeIds
		ORDER BY img.id asc
		""")
	List<PostImageSummary> findPartnershipImagesByPlaceIds(
		@Param("placeIds") List<Long> placeIds
	);
}
