package com.campus.campus.domain.studentcouncilpost.application.service;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostListItemResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostRequestDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.exception.EventEndDateTimeNotAllowedException;
import com.campus.campus.domain.studentcouncilpost.application.exception.EventStartDateTimeRequiredException;
import com.campus.campus.domain.studentcouncilpost.application.exception.NotPostWriterException;
import com.campus.campus.domain.studentcouncilpost.application.exception.PartnershipDateRequiredException;
import com.campus.campus.domain.studentcouncilpost.application.exception.PostNotFoundException;
import com.campus.campus.domain.studentcouncilpost.application.exception.ThumbnailRequiredException;
import com.campus.campus.domain.studentcouncilpost.application.mapper.StudentCouncilPostMapper;
import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.PostImage;
import com.campus.campus.domain.studentcouncilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.studentcouncilpost.domain.repository.PostImageRepository;
import com.campus.campus.domain.studentcouncilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.global.oci.OciPresignedUrlService;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        validateCategoryDateRule(dto);
        NormalizedDateTime normalized =
                normalizeDateTime(
                        dto.category(),
                        dto.startDateTime(),
                        dto.endDateTime()
                );

        StudentCouncilPost post = StudentCouncilPost.builder()
                .writer(writer)
                .category(dto.category())
                .title(dto.title())
                .content(dto.content())
                .place(dto.place())
                .startDateTime(normalized.startDateTime())
                .endDateTime(normalized.endDateTime())
                .thumbnailImageUrl(dto.thumbnailImageUrl())
                .thumbnailIcon(dto.thumbnailIcon())
                .build();

        post = postRepository.save(post);

        savePostImages(post, dto.imageUrls());

        return StudentCouncilPostMapper.toDetail(
                post,
                getPostImageUrls(post),
                councilId
        );
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
        validateCategoryDateRule(dto);

        NormalizedDateTime normalized =
                normalizeDateTime(
                        dto.category(),
                        dto.startDateTime(),
                        dto.endDateTime()
                );

        String oldThumbnailUrl = post.getThumbnailImageUrl();
        List<PostImage> oldImages = postImageRepository.findAllByPost(post);

        post.update(
                dto.title(),
                dto.content(),
                dto.place(),
                normalized.startDateTime(),
                normalized.endDateTime(),
                dto.thumbnailImageUrl(),
                dto.thumbnailIcon(),
                dto.category()
        );

        postImageRepository.deleteByPost(post);
        savePostImages(post, dto.imageUrls());

        cleanupUnusedImages(oldThumbnailUrl, oldImages, dto);

        return StudentCouncilPostMapper.toDetail(
                post,
                getPostImageUrls(post),
                councilId
        );
    }



    //이미지 삭제
    private void cleanupUnusedImages(
            String oldThumbnailUrl,
            List<PostImage> oldImages,
            PostRequestDto dto
    ) {
        if (oldThumbnailUrl != null &&
                !oldThumbnailUrl.equals(dto.thumbnailImageUrl())) {
            deleteImageSafely(oldThumbnailUrl);
        }

        List<String> newUrls = dto.imageUrls() == null
                ? List.of()
                : dto.imageUrls();

        oldImages.stream()
                .map(PostImage::getImageUrl)
                .filter(url -> !newUrls.contains(url))
                .forEach(this::deleteImageSafely);
    }

    private void deleteImageSafely(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            presignedUrlService.deleteImage(imageUrl);
        } catch (Exception e) {
            log.warn("OCI 파일 삭제 실패 (파일이 없을 수 있음): {}", imageUrl);
        }
    }

    private void savePostImages(StudentCouncilPost post, List<String> imageUrls) {
        if (imageUrls == null) return;

        for (String imageUrl : imageUrls) {
            postImageRepository.save(
                    PostImage.builder()
                            .post(post)
                            .imageUrl(imageUrl)
                            .build()
            );
        }
    }

    private List<String> getPostImageUrls(StudentCouncilPost post) {
        return postImageRepository
                .findAllByPostOrderByIdAsc(post)
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

    private NormalizedDateTime normalizeDateTime(
            PostCategory category,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (category == PostCategory.PARTNERSHIP) {
            return new NormalizedDateTime(
                    start.with(LocalTime.MIN),
                    end.with(LocalTime.MAX)
            );
        }

        // EVENT
        return new NormalizedDateTime(start, null);
    }

    private record NormalizedDateTime(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {}

    private void validateWriter(Long councilId, StudentCouncilPost post) {
        if (!post.getWriter().getId().equals(councilId)) throw new NotPostWriterException();
    }

    private void validateThumbnail(PostRequestDto dto) {
        if (dto.thumbnailImageUrl() == null && dto.thumbnailIcon() == null) {
            throw new ThumbnailRequiredException();
        }
    }

    private void validateCategoryDateRule(PostRequestDto dto) {
        if (dto.category() == PostCategory.EVENT) {
            if (dto.startDateTime() == null) {
                throw new EventStartDateTimeRequiredException();
            }
            if (dto.endDateTime() != null) {
                throw new EventEndDateTimeNotAllowedException();
            }
        }

        if (dto.category() == PostCategory.PARTNERSHIP) {
            if (dto.startDateTime() == null || dto.endDateTime() == null) {
                throw new PartnershipDateRequiredException();
            }
        }
    }


}