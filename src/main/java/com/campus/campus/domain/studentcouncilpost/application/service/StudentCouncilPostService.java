package com.campus.campus.domain.studentcouncilpost.application.service;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostImageProcessEvent;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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

    private static final int MIN_PAGE_INDEX = 0;
    private static final int FIRST_SEQUENCE = 1;
    private static final String TEMP_PATH = "/temp/";
    private static final String POSTS_PATH_FORMAT = "/posts/%d/";

    private final StudentCouncilPostRepository postRepository;
    private final StudentCouncilRepository studentCouncilRepository;
    private final PostImageRepository postImageRepository;
    private final OciPresignedUrlService presignedUrlService;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public PostResponseDto create(Long councilId, PostRequestDto dto) {
        StudentCouncil writer = findStudentCouncil(councilId);
        validateThumbnail(dto);

        StudentCouncilPost post = createPost(writer, dto);
        post = postRepository.save(post);

        updatePostThumbnail(post, dto.getThumbnailImageUrl());
        List<String> finalImageUrls = savePostImages(post, dto.getImageUrls());

        publishImageProcessEvent(post.getId(), dto.getThumbnailImageUrl(), dto.getImageUrls());

        return StudentCouncilPostMapper.toDetail(post, finalImageUrls);
    }

    @Transactional(readOnly = true)
    public PostResponseDto findById(Long postId) {
        StudentCouncilPost post = findPost(postId);
        List<String> finalImageUrls = getFinalImageUrls(post);

        return StudentCouncilPostMapper.toDetail(post, finalImageUrls);
    }

    @Transactional(readOnly = true)
    public Page<PostListItemResponseDto> findAll(PostCategory category, int page, int size) {
        Pageable pageable = createPageable(page, size);
        Page<StudentCouncilPost> posts = findPostsByCategory(category, pageable);

        return posts.map(StudentCouncilPostMapper::toListItem);
    }

    @Transactional
    public void delete(Long councilId, Long postId) {
        StudentCouncilPost post = findPost(postId);
        validateWriter(councilId, post);

        List<PostImage> postImages = postImageRepository.findAllByPost(post);

        deleteImagesFromBucket(post.getThumbnailImageUrl(), postImages);
        deletePostFromDatabase(post, postImages);

        log.info("게시글 및 관련 이미지 삭제 완료 - Post ID: {}", postId);
    }

    @Transactional
    public PostResponseDto update(Long councilId, Long postId, PostRequestDto dto) {
        StudentCouncilPost post = findPost(postId);
        validateWriter(councilId, post);
        validateThumbnail(dto);

        ImageUpdateContext context = prepareImageUpdateContext(post);
        updatePostContent(post, dto);
        List<String> finalImageUrls = updatePostImages(post, dto.getImageUrls());
        cleanupOldImages(context, post.getThumbnailImageUrl(), finalImageUrls);

        publishImageProcessEvent(post.getId(), dto.getThumbnailImageUrl(), dto.getImageUrls());

        return StudentCouncilPostMapper.toDetail(post, finalImageUrls);
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
                .thumbnailIcon(dto.getThumbnailIcon())
                .build();
    }

    private void updatePostThumbnail(StudentCouncilPost post, String thumbnailUrl) {
        String finalThumbnailUrl = convertToFinalUrl(thumbnailUrl, post.getId());
        post.updateThumbnail(finalThumbnailUrl);
    }

    private List<String> savePostImages(StudentCouncilPost post, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> finalImageUrls = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            String finalUrl = convertToFinalUrl(imageUrls.get(i), post.getId());

            postImageRepository.save(createPostImage(post, finalUrl, i + FIRST_SEQUENCE));
            finalImageUrls.add(finalUrl);
        }

        return finalImageUrls;
    }

    private PostImage createPostImage(StudentCouncilPost post, String finalUrl, int sequence) {
        return PostImage.builder()
                .post(post)
                .finalUrl(finalUrl)
                .sequence(sequence)
                .status(ImageStatus.FINAL)
                .build();
    }

    private List<String> getFinalImageUrls(StudentCouncilPost post) {
        return postImageRepository
                .findAllByPostAndStatusOrderBySequenceAsc(post, ImageStatus.FINAL)
                .stream()
                .map(PostImage::getFinalUrl)
                .collect(Collectors.toList());
    }

    private Pageable createPageable(int page, int size) {
        int pageIndex = Math.max(page - 1, MIN_PAGE_INDEX);
        return PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private Page<StudentCouncilPost> findPostsByCategory(PostCategory category, Pageable pageable) {
        return (category == null)
                ? postRepository.findAll(pageable)
                : postRepository.findAllByCategory(category, pageable);
    }

    private void deleteImagesFromBucket(String thumbnailUrl, List<PostImage> postImages) {
        deleteImageSafely(thumbnailUrl);

        postImages.stream()
                .map(PostImage::getFinalUrl)
                .forEach(this::deleteImageSafely);
    }

    private void deleteImageSafely(String imageUrl) {
        if (imageUrl == null) {
            return;
        }

        try {
            presignedUrlService.deleteImage(imageUrl);
        } catch (Exception e) {
            log.warn("OCI 파일 삭제 실패: {}", imageUrl, e);
        }
    }

    private void deletePostFromDatabase(StudentCouncilPost post, List<PostImage> postImages) {
        postImageRepository.deleteAll(postImages);
        postRepository.delete(post);
    }

    private ImageUpdateContext prepareImageUpdateContext(StudentCouncilPost post) {
        List<PostImage> oldImages = postImageRepository.findAllByPost(post);
        String oldThumbnailUrl = post.getThumbnailImageUrl();

        return new ImageUpdateContext(oldThumbnailUrl, oldImages);
    }

    private void updatePostContent(StudentCouncilPost post, PostRequestDto dto) {
        String finalThumbnailUrl = convertToFinalUrl(dto.getThumbnailImageUrl(), post.getId());

        post.update(
                dto.getTitle(),
                dto.getContent(),
                dto.getPlace(),
                dto.getStartDate(),
                dto.getEndDate(),
                finalThumbnailUrl,
                dto.getThumbnailIcon(),
                dto.getCategory()
        );
    }

    private List<String> updatePostImages(StudentCouncilPost post, List<String> imageUrls) {
        postImageRepository.deleteByPost(post);
        return savePostImages(post, imageUrls);
    }

    private void cleanupOldImages(ImageUpdateContext context, String newThumbnailUrl, List<String> newImageUrls) {
        cleanupOldThumbnail(context.getOldThumbnailUrl(), newThumbnailUrl);
        cleanupOldPostImages(context.getOldImages(), newImageUrls);
    }

    private void cleanupOldThumbnail(String oldThumbnailUrl, String newThumbnailUrl) {
        if (oldThumbnailUrl != null && !oldThumbnailUrl.equals(newThumbnailUrl)) {
            deleteImageSafely(oldThumbnailUrl);
        }
    }
    private void cleanupOldPostImages(List<PostImage> oldImages, List<String> newImageUrls) {
        oldImages.stream()
                .map(PostImage::getFinalUrl)
                .filter(url -> !newImageUrls.contains(url))
                .forEach(this::deleteImageSafely);
    }

    private void publishImageProcessEvent(Long postId, String thumbnailUrl, List<String> imageUrls) {
        eventPublisher.publishEvent(new PostImageProcessEvent(postId, thumbnailUrl, imageUrls));
    }

    private String convertToFinalUrl(String tempUrl, Long postId) {
        if (tempUrl == null || !tempUrl.contains(TEMP_PATH)) {
            return tempUrl;
        }

        String finalPath = String.format(POSTS_PATH_FORMAT, postId);
        return tempUrl.replace(TEMP_PATH, finalPath);
    }

    private StudentCouncil findStudentCouncil(Long councilId) {
        return studentCouncilRepository.findById(councilId)
                .orElseThrow(StudentCouncilNotFoundException::new);
    }

    private StudentCouncilPost findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    private void validateWriter(Long councilId, StudentCouncilPost post) {
        if (!post.getWriter().getId().equals(councilId)) {
            throw new NotPostWriterException();
        }
    }

    private void validateThumbnail(PostRequestDto dto) {
        if (dto.getThumbnailImageUrl() == null && dto.getThumbnailIcon() == null) {
            throw new ThumbnailRequiredException();
        }
    }

    @Getter
    @AllArgsConstructor
    private static class ImageUpdateContext {
        private final String oldThumbnailUrl;
        private final List<PostImage> oldImages;
    }
}