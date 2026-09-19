package com.campus.campus.domain.review.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.campus.campus.domain.review.application.dto.request.ReviewReportRequest;
import com.campus.campus.domain.review.application.exception.AlreadyReportedException;
import com.campus.campus.domain.review.application.exception.ReviewNotFoundException;
import com.campus.campus.domain.review.domain.entity.ReviewReport;
import com.campus.campus.domain.review.domain.repository.ReviewReportRepository;
import com.campus.campus.domain.review.domain.repository.ReviewRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ReviewReportRepository reviewReportRepository;

	@InjectMocks
	private ReviewService reviewService;

	@Test
	void 존재하지_않는_리뷰를_신고하면_예외를_던진다() {
		when(reviewRepository.existsById(1L)).thenReturn(false);

		ReviewReportRequest request = new ReviewReportRequest("욕설이 포함되어 있어요");

		assertThatThrownBy(() -> reviewService.reportReview(10L, 1L, request))
			.isInstanceOf(ReviewNotFoundException.class);

		verifyNoInteractions(reviewReportRepository);
	}

	@Test
	void 존재하지_않는_유저가_신고하면_예외를_던진다() {
		when(reviewRepository.existsById(1L)).thenReturn(true);
		when(reviewReportRepository.existsByReporter_IdAndReviewId(10L, 1L)).thenReturn(false);
		when(userRepository.findById(10L)).thenReturn(Optional.empty());

		ReviewReportRequest request = new ReviewReportRequest("욕설이 포함되어 있어요");

		assertThatThrownBy(() -> reviewService.reportReview(10L, 1L, request))
			.isInstanceOf(UserNotFoundException.class);

		verify(reviewReportRepository, never()).save(any());
	}

	@Test
	void 이미_신고한_리뷰면_예외를_던진다() {
		when(reviewRepository.existsById(1L)).thenReturn(true);
		when(reviewReportRepository.existsByReporter_IdAndReviewId(10L, 1L)).thenReturn(true);

		ReviewReportRequest request = new ReviewReportRequest("욕설이 포함되어 있어요");

		assertThatThrownBy(() -> reviewService.reportReview(10L, 1L, request))
			.isInstanceOf(AlreadyReportedException.class);

		verifyNoInteractions(userRepository);
		verify(reviewReportRepository, never()).save(any());
	}

	@Test
	void 정상_요청이면_신고내역을_저장한다() {
		User reporter = User.builder().id(10L).build();
		when(reviewRepository.existsById(1L)).thenReturn(true);
		when(reviewReportRepository.existsByReporter_IdAndReviewId(10L, 1L)).thenReturn(false);
		when(userRepository.findById(10L)).thenReturn(Optional.of(reporter));

		ReviewReportRequest request = new ReviewReportRequest("욕설이 포함되어 있어요");

		reviewService.reportReview(10L, 1L, request);

		ArgumentCaptor<ReviewReport> captor = ArgumentCaptor.forClass(ReviewReport.class);
		verify(reviewReportRepository).save(captor.capture());

		ReviewReport saved = captor.getValue();
		assertThat(saved.getReporter()).isSameAs(reporter);
		assertThat(saved.getReviewId()).isEqualTo(1L);
		assertThat(saved.getReason()).isEqualTo("욕설이 포함되어 있어요");
	}
}
