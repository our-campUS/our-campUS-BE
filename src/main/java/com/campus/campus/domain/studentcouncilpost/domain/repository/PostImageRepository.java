package com.campus.campus.domain.studentcouncilpost.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.studentcouncilpost.domain.entity.PostImage;
import com.campus.campus.domain.studentcouncilpost.domain.entity.StudentCouncilPost;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	List<PostImage> findAllByPost(StudentCouncilPost post);

	void deleteByPost(StudentCouncilPost post);

	List<PostImage> findAllByPostOrderByIdAsc(StudentCouncilPost post);
}

