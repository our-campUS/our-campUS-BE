package com.campus.campus.domain.studentcouncilpost.application.service;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostListItemResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostRequestDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.exception.NotPostWriterException;
import com.campus.campus.domain.studentcouncilpost.application.exception.PostNotFoundException;
import com.campus.campus.domain.studentcouncilpost.application.exception.ThumbnailRequiredException;
import com.campus.campus.domain.studentcouncilpost.application.mapper.StudentCouncilPostMapper;
import com.campus.campus.domain.studentcouncilpost.domain.entity.ImageStatus;
import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.PostImage;
import com.campus.campus.domain.studentcouncilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.studentcouncilpost.domain.repository.PostImageRepository;
import com.campus.campus.domain.studentcouncilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.global.oci.OciPresignedUrlService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentCouncilPostService {

    private final StudentCouncilPostRepository postRepository;
    private final StudentCouncilRepository studentCouncilRepository;
    private final PostImageRepository postImageRepository;
    private final OciPresignedUrlService presignedUrlService;

    @Transactional
    public PostResponseDto create(Long councilId, PostRequestDto dto) {
        StudentCouncil writer = findStudentCouncil(councilId);
        validateThumbnail(dto);

        StudentCouncilPost post = createPost(writer, dto);
        post = postRepository.save(post);

        List<String> imageUrls = dto.getImageUrls() != null
                ? dto.getImageUrls()
                : new ArrayList<>();

        for (int i = 0; i < imageUrls.size(); i++) {
            postImageRepository.save(PostImage.builder()
                    .post(post)
                    .imageUrl(imageUrls.get(i))
                    .sequence(i + 1)
                    .build());
        }

        return StudentCouncilPostMapper.toDetail(post, imageUrls, councilId);
    }


    @Transactional(readOnly = true)
    public PostResponseDto findById(Long postId, Long currentUserId) {
        StudentCouncilPost post = postRepository.findByIdWithFullInfo(postId)
                .orElseThrow(PostNotFoundException::new);

        List<String> imageUrls = getPostImageUrls(post);

        return StudentCouncilPostMapper.toDetail(post, imageUrls, currentUserId);
    }

    @Transactional(readOnly = true)
    public Page<PostListItemResponseDto> findAll(
            PostCategory category,
            int page,
            int size,
            Long currentUserId
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page - 1, 0),
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<StudentCouncilPost> posts = (category == null)
                ? postRepository.findAll(pageable)
                : postRepository.findAllByCategory(category, pageable);

        return posts.map(post ->
                StudentCouncilPostMapper.toListItem(post, currentUserId)
        );
    }


    @Transactional
    public void delete(Long councilId, Long postId) {
        StudentCouncilPost post = findPost(postId);
        validateWriter(councilId, post);

        List<PostImage> postImages = postImageRepository.findAllByPost(post);

        // 버킷에서 실제 파일 삭제
        deleteImageSafely(post.getThumbnailImageUrl());
        postImages.forEach(img -> deleteImageSafely(img.getImageUrl()));

        postImageRepository.deleteAll(postImages);
        postRepository.delete(post);
    }

    @Transactional
    public PostResponseDto update(
            Long councilId,
            Long postId,
            PostRequestDto dto
    ) {
        StudentCouncilPost post = findPost(postId);
        validateWriter(councilId, post);
        validateThumbnail(dto);

        String oldThumbnailUrl = post.getThumbnailImageUrl();
        List<PostImage> oldImages = postImageRepository.findAllByPost(post);

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

        postImageRepository.deleteByPost(post);

        List<String> newUrls = dto.getImageUrls() != null
                ? dto.getImageUrls()
                : new ArrayList<>();

        for (int i = 0; i < newUrls.size(); i++) {
            postImageRepository.save(PostImage.builder()
                    .post(post)
                    .imageUrl(newUrls.get(i))
                    .sequence(i + 1)
                    .build());
        }

        if (oldThumbnailUrl != null &&
                !oldThumbnailUrl.equals(dto.getThumbnailImageUrl())) {
            deleteImageSafely(oldThumbnailUrl);
        }

        oldImages.stream()
                .map(PostImage::getImageUrl)
                .filter(url -> !newUrls.contains(url))
                .forEach(this::deleteImageSafely);

        return StudentCouncilPostMapper.toDetail(post, newUrls, councilId);
    }


    private StudentCouncilPost createPost(StudentCouncil writer, PostRequestDto dto) {
        return StudentCouncilPost.builder()
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
    }

    private void deleteImageSafely(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            presignedUrlService.deleteImage(imageUrl);
        } catch (Exception e) {
            log.warn("OCI 파일 삭제 실패 (파일이 없을 수 있음): {}", imageUrl);
        }
    }


    private List<String> getPostImageUrls(StudentCouncilPost post) {
        return postImageRepository
                .findAllByPostOrderBySequenceAsc(post)
                .stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
    }

    private StudentCouncil findStudentCouncil(Long councilId) {
        return studentCouncilRepository.findById(councilId).orElseThrow(StudentCouncilNotFoundException::new);
    }

    private StudentCouncilPost findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
    }

    private void validateWriter(Long councilId, StudentCouncilPost post) {
        if (!post.getWriter().getId().equals(councilId)) throw new NotPostWriterException();
    }

    private void validateThumbnail(PostRequestDto dto) {
        if (dto.getThumbnailImageUrl() == null && dto.getThumbnailIcon() == null) {
            throw new ThumbnailRequiredException();
        }
    }
}