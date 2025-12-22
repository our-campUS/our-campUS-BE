package com.campus.campus.domain.studentcouncilpost.domain.repository;

import com.campus.campus.domain.studentcouncilpost.domain.entity.ImageStatus;
import com.campus.campus.domain.studentcouncilpost.domain.entity.PostImage;
import com.campus.campus.domain.studentcouncilpost.domain.entity.StudentCouncilPost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostIdOrderBySequenceAsc(Long postId);
    List<PostImage> findAllByPostAndStatusOrderBySequenceAsc(StudentCouncilPost post, ImageStatus status);
    List<PostImage> findAllByPost(StudentCouncilPost post);
    void deleteByPost(StudentCouncilPost post);
}

