package com.campus.campus.domain.councilpost.application.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.councilpost.application.dto.response.GetActivePartnershipListForUserResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostListItemResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostResponse;
import com.campus.campus.domain.councilpost.application.exception.CollegeNotSetException;
import com.campus.campus.domain.councilpost.application.exception.MajorNotSetException;
import com.campus.campus.domain.councilpost.application.exception.PostNotFoundException;
import com.campus.campus.domain.councilpost.application.mapper.StudentCouncilPostMapper;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.repository.PostImageRepository;
import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.councilpost.policy.PostAccessPolicy;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentCouncilPostForUserService {

	private static final int UPCOMING_HOURS = 72;
	private static final ZoneId KST = ZoneId.of("Asia/Seoul");
	private final StudentCouncilPostRepository studentCouncilPostRepository;
	private final PostImageRepository postImageRepository;
	private final StudentCouncilPostMapper studentCouncilPostMapper;
	private final UserRepository userRepository;
	private final PostAccessPolicy postAccessPolicy;

	public Page<PostListItemResponse> findSchoolPosts(PostCategory category, int page, int size, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "startDateTime"));

		Page<StudentCouncilPost> posts = studentCouncilPostRepository
			.findBySchoolId(user.getSchool().getSchoolId(), category, CouncilType.SCHOOL_COUNCIL, pageable);

		return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, userId));
	}

	public Page<PostListItemResponse> findCollegePosts(PostCategory category, int page, int size, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		if (user.isProfileNotCompleted() || user.getCollege() == null) {
			throw new CollegeNotSetException();
		}

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "startDateTime"));

		Page<StudentCouncilPost> posts = studentCouncilPostRepository
			.findByCollegeId(user.getCollege().getCollegeId(), category, CouncilType.COLLEGE_COUNCIL, pageable);

		return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, userId));
	}

	public Page<PostListItemResponse> findMajorPosts(PostCategory category, int page, int size, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "startDateTime"));

		Page<StudentCouncilPost> posts = studentCouncilPostRepository
			.findByMajorId(user.getMajor().getMajorId(), category, CouncilType.MAJOR_COUNCIL, pageable);

		return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, userId));
	}

	public Page<PostListItemResponse> findUpcomingSchoolEvents72h(int page, int size, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.ASC, "startDateTime"));

		LocalDateTime now = LocalDateTime.now(KST);
		LocalDateTime limit = now.plusHours(UPCOMING_HOURS);

		Page<StudentCouncilPost> posts = studentCouncilPostRepository.findUpcomingSchoolEvents(
			user.getSchool().getSchoolId(),
			PostCategory.EVENT,
			CouncilType.SCHOOL_COUNCIL,
			now,
			limit,
			pageable
		);

		return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, userId));
	}

	public Page<PostListItemResponse> findUpcomingCollegeEvents72h(int page, int size, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		if (user.isProfileNotCompleted() || user.getCollege() == null) {
			throw new CollegeNotSetException();
		}

		Pageable pageable = PageRequest.of(
			Math.max(page - 1, 0),
			size,
			Sort.by(Sort.Direction.ASC, "startDateTime")
		);

		LocalDateTime now = LocalDateTime.now(KST);
		LocalDateTime limit = now.plusHours(UPCOMING_HOURS);

		Page<StudentCouncilPost> posts = studentCouncilPostRepository.findUpcomingCollegeEvents(
			user.getCollege().getCollegeId(),
			PostCategory.EVENT,
			CouncilType.COLLEGE_COUNCIL,
			now,
			limit,
			pageable
		);

		return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, userId));
	}

	public Page<PostListItemResponse> findUpcomingMajorEvents72h(int page, int size, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		if (user.isProfileNotCompleted() || user.getMajor() == null) {
			throw new MajorNotSetException();
		}

		Pageable pageable = PageRequest.of(
			Math.max(page - 1, 0),
			size,
			Sort.by(Sort.Direction.ASC, "startDateTime")
		);

		LocalDateTime now = LocalDateTime.now(KST);
		LocalDateTime limit = now.plusHours(UPCOMING_HOURS);

		Page<StudentCouncilPost> posts = studentCouncilPostRepository.findUpcomingMajorEvents(
			user.getMajor().getMajorId(),
			PostCategory.EVENT,
			CouncilType.MAJOR_COUNCIL,
			now,
			limit,
			pageable
		);

		return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, userId));
	}

	public PostResponse findById(Long postId, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

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

	public List<GetActivePartnershipListForUserResponse> findActivePartnershipForUser(CouncilType councilType,
		Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId)
			.orElseThrow(UserNotFoundException::new);

		if (user.getSchool() == null) {
			return List.of();
		}

		Long schoolId = user.getSchool().getSchoolId();
		Long collegeId = null;
		Long majorId = null;

		if (CouncilType.COLLEGE_COUNCIL.equals(councilType)) {
			if (user.getCollege() == null) {
				return List.of();
			}
			collegeId = user.getCollege().getCollegeId();
		}

		if (CouncilType.MAJOR_COUNCIL.equals(councilType)) {
			if (user.getMajor() == null) {
				return List.of();
			}
			majorId = user.getMajor().getMajorId();
		}

		LocalDateTime now = LocalDateTime.now(KST);
		Pageable partnershipCount = PageRequest.of(0, 3);

		List<StudentCouncilPost> partnerships = studentCouncilPostRepository.findRandomActivePartnerships(schoolId,
			councilType, PostCategory.PARTNERSHIP, collegeId, majorId, now, partnershipCount);

		return partnerships.stream()
			.map(studentCouncilPostMapper::toGetActivePartnershipListForUserResponse)
			.toList();
	}
}
