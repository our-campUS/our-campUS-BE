package com.campus.campus.domain.councilpost.application.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.councilpost.application.dto.response.GetActivePartnershipListForUserResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetLikedPostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostForUserResponse;
import com.campus.campus.domain.councilpost.application.dto.response.LikePostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostListItemResponse;
import com.campus.campus.domain.councilpost.application.dto.response.TodayEventResponse;
import com.campus.campus.domain.councilpost.application.exception.CollegeNotSetException;
import com.campus.campus.domain.councilpost.application.exception.MajorNotSetException;
import com.campus.campus.domain.councilpost.application.exception.PostNotFoundException;
import com.campus.campus.domain.councilpost.application.mapper.StudentCouncilPostMapper;
import com.campus.campus.domain.councilpost.domain.entity.LikePost;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.repository.LikePostRepository;
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
	private final LikePostRepository likePostRepository;
	private final StudentCouncilPostMapper studentCouncilPostMapper;
	private final UserRepository userRepository;
	private final PostAccessPolicy postAccessPolicy;

	@Transactional
	public LikePostResponse toggleLikePost(Long userId, Long postId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		StudentCouncilPost post = studentCouncilPostRepository.findById(postId)
			.orElseThrow(PostNotFoundException::new);

		int deleted = likePostRepository.deleteByUserIdAndPostId(userId, postId);
		if (deleted > 0) {
			return studentCouncilPostMapper.toLikePostResponse(user, post, false);
		}

		try {
			likePostRepository.saveAndFlush(studentCouncilPostMapper.createLikePost(user, post));
			return studentCouncilPostMapper.toLikePostResponse(user, post, true);
		} catch (DataIntegrityViolationException e) {
			return studentCouncilPostMapper.toLikePostResponse(user, post, true);
		}
	}

	public Page<GetLikedPostResponse> findLikedPosts(PostCategory category, int page, int size, Long userId) {
		userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);

		Page<LikePost> likedPosts = likePostRepository.findLikedPosts(userId, category, pageable);

		return likedPosts.map(likePost ->
			studentCouncilPostMapper.toGetLikedPostResponse(likePost.getPost())
		);
	}

	public GetPostForUserResponse findById(Long postId, Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		StudentCouncilPost post = studentCouncilPostRepository.findByIdWithFullInfo(postId)
			.orElseThrow(PostNotFoundException::new);

		// 권한 검증
		postAccessPolicy.validateAccess(user, post.getWriter());

		List<String> imageUrls = postImageRepository.findAllByPostOrderByIdAsc(post)
			.stream()
			.map(PostImage::getImageUrl)
			.toList();

		boolean isLiked = likePostRepository.existsByUserIdAndPost_Id(userId, postId);

		return studentCouncilPostMapper.toGetPostForUserResponse(post, imageUrls, userId, isLiked);
	}

	public Page<PostListItemResponse> findSchoolPosts(PostCategory category, int page, int size, Long userId,
		Long excludePostId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "startDateTime"));

		Page<StudentCouncilPost> posts = studentCouncilPostRepository
			.findBySchoolId(user.getSchool().getSchoolId(), category, CouncilType.SCHOOL_COUNCIL, excludePostId,
				pageable);

		return mapPostsWithLikes(posts, userId);
	}

	public Page<PostListItemResponse> findCollegePosts(PostCategory category, int page, int size, Long userId,
		Long excludePostId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		if (user.isProfileNotCompleted() || user.getCollege() == null) {
			throw new CollegeNotSetException();
		}

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "startDateTime"));

		Page<StudentCouncilPost> posts = studentCouncilPostRepository
			.findByCollegeId(user.getCollege().getCollegeId(), category, CouncilType.COLLEGE_COUNCIL, excludePostId,
				pageable);

		return mapPostsWithLikes(posts, userId);
	}

	public Page<PostListItemResponse> findMajorPosts(PostCategory category, int page, int size, Long userId,
		Long excludePostId) {
		User user = userRepository.findByIdWithAcademicInfo(userId).orElseThrow(UserNotFoundException::new);

		if (user.isProfileNotCompleted() || user.getMajor() == null) {
			throw new MajorNotSetException();
		}

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "startDateTime"));

		Page<StudentCouncilPost> posts = studentCouncilPostRepository
			.findByMajorId(user.getMajor().getMajorId(), category, CouncilType.MAJOR_COUNCIL, excludePostId, pageable);

		return mapPostsWithLikes(posts, userId);
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

		return mapPostsWithLikes(posts, userId);
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

		return mapPostsWithLikes(posts, userId);
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

		return mapPostsWithLikes(posts, userId);
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

	public TodayEventResponse findTodayEvent(Long userId) {
		User user = userRepository.findByIdWithAcademicInfo(userId)
			.orElseThrow(UserNotFoundException::new);

		if (user.getSchool() == null) {
			return null;
		}

		Long schoolId = user.getSchool().getSchoolId();
		Long collegeId = user.getCollege() != null ? user.getCollege().getCollegeId() : null;
		Long majorId = user.getMajor() != null ? user.getMajor().getMajorId() : null;

		LocalDateTime now = LocalDateTime.now(KST);
		LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = now.toLocalDate().atTime(LocalTime.MAX);

		List<StudentCouncilPost> events = studentCouncilPostRepository.findTodayEvent(
			schoolId, collegeId, majorId,
			PostCategory.EVENT,
			startOfDay, endOfDay,
			PageRequest.of(0, 1)
		);

		if (events.isEmpty()) {
			return null;
		}

		return studentCouncilPostMapper.toTodayRandomEventResponse(events.getFirst());
	}

	private Page<PostListItemResponse> mapPostsWithLikes(Page<StudentCouncilPost> posts, Long userId) {
		List<Long> postIds = posts.getContent().stream()
			.map(StudentCouncilPost::getId)
			.toList();

		if (postIds.isEmpty()) {
			return posts.map(post -> studentCouncilPostMapper.toPostListItemResponse(post, false));
		}

		Set<Long> likedPostIds = new HashSet<>(likePostRepository.findLikedPostIds(userId, postIds));

		return posts.map(post ->
			studentCouncilPostMapper.toPostListItemResponse(post, likedPostIds.contains(post.getId()))
		);
	}
}
