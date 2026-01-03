package com.campus.campus.domain.councilnotice.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.councilnotice.application.dto.request.NoticeRequest;
import com.campus.campus.domain.councilnotice.application.dto.response.NoticeListItemResponse;
import com.campus.campus.domain.councilnotice.application.dto.response.NoticeResponse;
import com.campus.campus.domain.councilnotice.application.exception.NotNoticeWriterException;
import com.campus.campus.domain.councilnotice.application.exception.NoticeImageLimitExceededException;
import com.campus.campus.domain.councilnotice.application.exception.NoticeNotFoundException;
import com.campus.campus.domain.councilnotice.application.exception.NoticeOciImageDeleteFailedException;
import com.campus.campus.domain.councilnotice.application.mapper.StudentCouncilNoticeMapper;
import com.campus.campus.domain.councilnotice.domain.entity.NoticeImage;
import com.campus.campus.domain.councilnotice.domain.entity.StudentCouncilNotice;
import com.campus.campus.domain.councilnotice.domain.repository.NoticeImageRepository;
import com.campus.campus.domain.councilnotice.domain.repository.StudentCouncilNoticeRepository;
import com.campus.campus.global.oci.application.service.PresignedUrlService;
import com.campus.campus.global.oci.mapper.PresignedUrlMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentCouncilNoticeService {

	private static final int MAX_IMAGE_COUNT = 10;

	private final StudentCouncilNoticeRepository noticeRepository;
	private final StudentCouncilRepository studentCouncilRepository;
	private final NoticeImageRepository noticeImageRepository;
	private final PresignedUrlService presignedUrlService;
	private final StudentCouncilNoticeMapper studentCouncilNoticeMapper;
	private final PresignedUrlMapper presignedUrlMapper;

	@Transactional
	public NoticeResponse create(Long councilId, NoticeRequest dto) {

		if (dto.imageUrls() != null && dto.imageUrls().size() > MAX_IMAGE_COUNT) {
			throw new NoticeImageLimitExceededException();
		}

		StudentCouncil writer = studentCouncilRepository.findByIdWithDetailsAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		StudentCouncilNotice notice =
			noticeRepository.save(studentCouncilNoticeMapper.createStudentCouncilNotice(writer, dto));

		if (dto.imageUrls() != null && !dto.imageUrls().isEmpty()) {
			List<NoticeImage> images = dto.imageUrls().stream()
				.map(imageUrl -> StudentCouncilNoticeMapper.createStudentCouncilNoticeImage(notice, imageUrl))
				.toList();

			noticeImageRepository.saveAll(images);
		}

		List<String> imageUrls = noticeImageRepository
			.findAllByNoticeOrderByIdAsc(notice)
			.stream()
			.map(NoticeImage::getImageUrl)
			.toList();

		return StudentCouncilNoticeMapper.toNoticeResponse(notice, imageUrls, councilId);
	}

	@Transactional(readOnly = true)
	public NoticeResponse findById(Long noticeId, Long councilId) {

		StudentCouncilNotice notice = noticeRepository.findByIdWithFullInfo(noticeId)
			.orElseThrow(NoticeNotFoundException::new);

		List<String> imageUrls = noticeImageRepository
			.findAllByNoticeOrderByIdAsc(notice)
			.stream()
			.map(NoticeImage::getImageUrl)
			.toList();

		return StudentCouncilNoticeMapper.toNoticeResponse(notice, imageUrls, councilId);
	}

	@Transactional(readOnly = true)
	public Page<NoticeListItemResponse> findAll(int page, int size, Long councilId) {

		Pageable pageable = PageRequest.of(
			Math.max(page - 1, 0),
			size,
			Sort.by(Sort.Direction.DESC, "createdAt")
		);

		Page<StudentCouncilNotice> notices = noticeRepository.findAllWithWriter(pageable);

		return notices.map(notice ->
			StudentCouncilNoticeMapper.toNoticeListItemResponse(notice, councilId)
		);
	}

	@Transactional
	public NoticeResponse update(Long councilId, Long noticeId, NoticeRequest dto) {

		if (dto.imageUrls() != null && dto.imageUrls().size() > MAX_IMAGE_COUNT) {
			throw new NoticeImageLimitExceededException();
		}

		StudentCouncilNotice notice = noticeRepository.findByIdWithFullInfo(noticeId)
			.orElseThrow(NoticeNotFoundException::new);

		if (!notice.isWrittenByCouncil(councilId)) {
			throw new NotNoticeWriterException();
		}

		// 업데이트 전 기존 이미지 스냅샷
		List<NoticeImage> oldImages = noticeImageRepository.findAllByNotice(notice);

		notice.update(dto.title(), dto.content());

		// DB 이미지 교체
		noticeImageRepository.deleteByNotice(notice);

		if (dto.imageUrls() != null) {
			for (String imageUrl : dto.imageUrls()) {
				noticeImageRepository.save(
					StudentCouncilNoticeMapper.createStudentCouncilNoticeImage(notice, imageUrl));
			}
		}

		cleanupUnusedImages(oldImages, dto);

		List<String> imageUrls = noticeImageRepository
			.findAllByNoticeOrderByIdAsc(notice)
			.stream()
			.map(NoticeImage::getImageUrl)
			.toList();

		return StudentCouncilNoticeMapper.toNoticeResponse(notice, imageUrls, councilId);
	}

	@Transactional
	public void delete(Long councilId, Long noticeId) {

		StudentCouncilNotice notice = noticeRepository.findByIdWithFullInfo(noticeId)
			.orElseThrow(NoticeNotFoundException::new);

		if (!notice.isWrittenByCouncil(councilId)) {
			throw new NotNoticeWriterException();
		}

		List<NoticeImage> images = noticeImageRepository.findAllByNotice(notice);

		List<String> deleteTargets = images.stream()
			.map(NoticeImage::getImageUrl)
			.toList();

		noticeImageRepository.deleteAll(images);
		noticeRepository.delete(notice);

		for (String imageUrl : deleteTargets) {
			if (imageUrl == null || imageUrl.isBlank()) {
				continue;
			}

			try {
				presignedUrlService.deleteImage(imageUrl);
			} catch (NoticeOciImageDeleteFailedException e) {
				log.warn("OCI 파일 삭제 실패 (파일이 없을 수 있음): {}", imageUrl, e);
			}
		}
	}

	private void cleanupUnusedImages(List<NoticeImage> oldImages, NoticeRequest dto) {
		List<String> newUrls = dto.imageUrls() == null ? List.of() : dto.imageUrls();

		List<String> deleteTargets = oldImages.stream()
			.map(NoticeImage::getImageUrl)
			.filter(url -> !newUrls.contains(url))
			.toList();

		//삭제
		for (String imageUrl : deleteTargets) {
			if (imageUrl == null || imageUrl.isBlank()) {
				continue;
			}

			try {
				presignedUrlService.deleteImage(imageUrl);
			} catch (NoticeOciImageDeleteFailedException e) {
				log.warn("OCI 파일 삭제 실패 (파일이 없을 수 있음): {}", imageUrl, e);
			}
		}
	}
}
