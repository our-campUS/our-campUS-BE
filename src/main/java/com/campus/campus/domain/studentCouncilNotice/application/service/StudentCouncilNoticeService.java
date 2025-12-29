package com.campus.campus.domain.studentCouncilNotice.application.service;

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
import com.campus.campus.domain.studentCouncilNotice.application.dto.request.NoticeRequestDto;
import com.campus.campus.domain.studentCouncilNotice.application.dto.response.NoticeListItemResponseDto;
import com.campus.campus.domain.studentCouncilNotice.application.dto.response.NoticeResponseDto;
import com.campus.campus.domain.studentCouncilNotice.application.exception.NotNoticeWriterException;
import com.campus.campus.domain.studentCouncilNotice.application.exception.NoticeImageLimitExceededException;
import com.campus.campus.domain.studentCouncilNotice.application.exception.NoticeNotFoundException;
import com.campus.campus.domain.studentCouncilNotice.application.mapper.StudentCouncilNoticeMapper;
import com.campus.campus.domain.studentCouncilNotice.domain.entity.NoticeImage;
import com.campus.campus.domain.studentCouncilNotice.domain.entity.StudentCouncilNotice;
import com.campus.campus.domain.studentCouncilNotice.domain.repository.NoticeImageRepository;
import com.campus.campus.domain.studentCouncilNotice.domain.repository.StudentCouncilNoticeRepository;
import com.campus.campus.global.oci.application.service.PresignedUrlService;

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

	@Transactional
	public NoticeResponseDto create(Long councilId, NoticeRequestDto dto) {

		if (dto.imageUrls() != null && dto.imageUrls().size() > MAX_IMAGE_COUNT) {
			throw new NoticeImageLimitExceededException();
		}

		StudentCouncil writer = studentCouncilRepository.findByIdWithDetails(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		StudentCouncilNotice notice =
			noticeRepository.save(StudentCouncilNoticeMapper.toEntity(writer, dto));

		if (dto.imageUrls() != null) {
			for (String imageUrl : dto.imageUrls()) {
				noticeImageRepository.save(StudentCouncilNoticeMapper.toEntity(notice, imageUrl));
			}
		}

		List<String> imageUrls = noticeImageRepository
			.findAllByNoticeOrderByIdAsc(notice)
			.stream()
			.map(NoticeImage::getImageUrl)
			.toList();

		return StudentCouncilNoticeMapper.toDetail(notice, imageUrls, councilId);
	}

	@Transactional(readOnly = true)
	public NoticeResponseDto findById(Long noticeId, Long councilId) {

		StudentCouncilNotice notice = noticeRepository.findByIdWithWriter(noticeId)
			.orElseThrow(NoticeNotFoundException::new);

		List<String> imageUrls = noticeImageRepository
			.findAllByNoticeOrderByIdAsc(notice)
			.stream()
			.map(NoticeImage::getImageUrl)
			.toList();

		return StudentCouncilNoticeMapper.toDetail(notice, imageUrls, councilId);
	}

	@Transactional(readOnly = true)
	public Page<NoticeListItemResponseDto> findAll(int page, int size, Long currentUserId) {

		Pageable pageable = PageRequest.of(
			Math.max(page - 1, 0),
			size,
			Sort.by(Sort.Direction.DESC, "createdAt")
		);

		Page<StudentCouncilNotice> notices = noticeRepository.findAll(pageable);

		return notices.map(notice ->
			StudentCouncilNoticeMapper.toListItem(notice, currentUserId)
		);
	}

	@Transactional
	public NoticeResponseDto update(Long councilId, Long noticeId, NoticeRequestDto dto) {

		if (dto.imageUrls() != null && dto.imageUrls().size() > MAX_IMAGE_COUNT) {
			throw new NoticeImageLimitExceededException();
		}

		StudentCouncilNotice notice = noticeRepository.findByIdWithWriter(noticeId)
			.orElseThrow(NoticeNotFoundException::new);

		if (!notice.isWrittenByCouncil(councilId)) {
			throw new NotNoticeWriterException();
		}

		List<NoticeImage> oldImages = noticeImageRepository.findAllByNotice(notice);

		notice.update(dto.title(), dto.content());

		noticeImageRepository.deleteByNotice(notice);

		if (dto.imageUrls() != null) {
			for (String imageUrl : dto.imageUrls()) {
				noticeImageRepository.save(
					StudentCouncilNoticeMapper.toEntity(notice, imageUrl)
				);
			}
		}

		for (NoticeImage image : oldImages) {
			try {
				presignedUrlService.deleteImage(image.getImageUrl());
			} catch (Exception e) {
				log.warn("OCI 파일 삭제 실패 (파일이 없을 수 있음): {}", image.getImageUrl());
			}
		}

		List<String> imageUrls = noticeImageRepository
			.findAllByNoticeOrderByIdAsc(notice)
			.stream()
			.map(NoticeImage::getImageUrl)
			.toList();

		return StudentCouncilNoticeMapper.toDetail(notice, imageUrls, councilId);
	}

	@Transactional
	public void delete(Long councilId, Long noticeId) {

		StudentCouncilNotice notice = noticeRepository.findByIdWithWriter(noticeId)
			.orElseThrow(NoticeNotFoundException::new);

		if (!notice.isWrittenByCouncil(councilId)) {
			throw new NotNoticeWriterException();
		}

		List<NoticeImage> images = noticeImageRepository.findAllByNotice(notice);

		noticeImageRepository.deleteAll(images);
		noticeRepository.delete(notice);

		for (NoticeImage image : images) {
			try {
				presignedUrlService.deleteImage(image.getImageUrl());
			} catch (Exception e) {
				log.warn("OCI 파일 삭제 실패 (파일이 없을 수 있음): {}", image.getImageUrl());
			}
		}
	}
}
