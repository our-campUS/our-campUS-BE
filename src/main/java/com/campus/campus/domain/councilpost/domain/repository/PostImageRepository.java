package com.campus.campus.domain.councilpost.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	List<PostImage> findAllByPost(StudentCouncilPost post);

	void deleteByPost(StudentCouncilPost post);

	List<PostImage> findAllByPostOrderByIdAsc(StudentCouncilPost post);

	@Query("""
			select pi.imageUrl
			from PostImage pi
			where pi.post = :post
			order by pi.id asc
		""")
	List<String> findImageUrlsByPost(@Param("post") StudentCouncilPost post);

	List<PostImage> findAllByPostIn(List<StudentCouncilPost> posts);
}
