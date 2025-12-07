package com.campus.campus.domain.studentcouncilpost.application.service;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostRequestDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.exception.PostNotFoundException;
import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.studentcouncilpost.application.exception.NotPostWriterException;
import com.campus.campus.domain.studentcouncilpost.application.exception.ThumbnailRequiredException;
import com.campus.campus.domain.studentcouncilpost.application.mapper.StudentCouncilPostMapper;
import com.campus.campus.domain.studentcouncilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.studentcouncilpost.domain.repository.StudentCouncilPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class StudentCouncilPostService {

    private final StudentCouncilPostRepository postRepository;
    private final StudentCouncilRepository studentCouncilRepository;
    public PostResponseDto create(Long councilId, PostRequestDto dto) {

        StudentCouncil writer = studentCouncilRepository.findById(councilId)
                .orElseThrow(StudentCouncilNotFoundException::new);

        validateThumbnail(dto);

        StudentCouncilPost post = StudentCouncilPost.builder()
                .writer(writer)
                .category(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .place(dto.getPlace())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .thumbnailImageUrl(dto.getThumbnailImageUrl())
                .thumbnailIcon(dto.getThumbnailIcon())
                .build();

        postRepository.save(post);

        return StudentCouncilPostMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public PostResponseDto get(Long postId) {
        StudentCouncilPost post = findPost(postId);
        return StudentCouncilPostMapper.toDto(post);
    }

    public PostResponseDto update(Long userId, Long postId, PostRequestDto dto) {

        StudentCouncilPost post = findPost(postId);
        validateWriter(userId, post);
        validateThumbnail(dto);

        post.update(
                dto.getTitle(),
                dto.getContent(),
                dto.getPlace(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getThumbnailImageUrl(),
                dto.getThumbnailIcon(),
                dto.getCategory()
        );

        return StudentCouncilPostMapper.toDto(post);
    }

    public void delete(Long userId, Long postId) {
        StudentCouncilPost post = findPost(postId);
        validateWriter(userId, post);
        postRepository.delete(post);
    }

    private StudentCouncilPost findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    private void validateWriter(Long userId, StudentCouncilPost post) {
        if (!post.getWriter().getId().equals(userId)) {
            throw new NotPostWriterException();
        }
    }


    private void validateThumbnail(PostRequestDto dto) {
        if (dto.getThumbnailImageUrl() == null && dto.getThumbnailIcon() == null) {
            throw new ThumbnailRequiredException();
        }
    }
}
