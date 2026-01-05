package com.campus.campus.domain.userpost.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.councilpost.application.dto.response.PostListItemResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostResponse;
import com.campus.campus.domain.councilpost.application.exception.PostNotFoundException;
import com.campus.campus.domain.councilpost.application.mapper.StudentCouncilPostMapper;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.repository.PostImageRepository;
import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.domain.userpost.policy.PostAccessPolicy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPostService {

	private final StudentCouncilPostRepository studentCouncilPostRepository;
	private final PostImageRepository postImageRepository;
	private final StudentCouncilPostMapper studentCouncilPostMapper;
	private final UserRepository userRepository;
	private final PostAccessPolicy postAccessPolicy;

	@Transactional(readOnly = true)
	public Page<PostListItemResponse> findSchoolPosts(PostCategory category, int page, int size, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId)
			.orElseThrow(UserNotFoundException::new);

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<StudentCouncilPost> posts = studentCouncilPostRepository
			.findBySchoolId(user.getSchool().getSchoolId(), category, pageable);

		return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, userId));
	}

	@Transactional(readOnly = true)
	public Page<PostListItemResponse> findCollegePosts(PostCategory category, int page, int size, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId)
			.orElseThrow(UserNotFoundException::new);

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<StudentCouncilPost> posts = studentCouncilPostRepository
			.findByCollegeId(user.getCollege().getCollegeId(), category, pageable);

		return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, userId));
	}

	@Transactional(readOnly = true)
	public Page<PostListItemResponse> findMajorPosts(PostCategory category, int page, int size, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId)
			.orElseThrow(UserNotFoundException::new);

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<StudentCouncilPost> posts = studentCouncilPostRepository
			.findByMajorId(user.getMajor().getMajorId(), category, pageable);

		return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, userId));
	}

	@Transactional(readOnly = true)
	public PostResponse findById(Long postId, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId)
			.orElseThrow(UserNotFoundException::new);

		StudentCouncilPost post = studentCouncilPostRepository.findByIdWithFullInfo(postId)
			.orElseThrow(PostNotFoundException::new);

		// 권한 검증
		postAccessPolicy.validateAccess(user, post.getWriter());

		List<String> imageUrls = postImageRepository.findAllByPostOrderByIdAsc(post)
			.stream()
			.map(PostImage::getImageUrl)
			.toList();

		return studentCouncilPostMapper.toPostResponse(post, imageUrls, userId);
	}
}
