package com.campus.campus.domain.inquiry.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.inquiry.application.dto.request.InquiryCreateRequest;
import com.campus.campus.domain.inquiry.application.dto.response.InquiryCreateResponse;
import com.campus.campus.domain.inquiry.application.dto.response.InquiryListItemResponse;
import com.campus.campus.domain.inquiry.application.mapper.InquiryMapper;
import com.campus.campus.domain.inquiry.domain.entity.Inquiry;
import com.campus.campus.domain.inquiry.domain.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentCouncilInquiryService {
	private final InquiryRepository inquiryRepository;
	private final InquiryMapper inquiryMapper;
	private final StudentCouncilRepository studentCouncilRepository;

	public InquiryCreateResponse createInquiry(Long councilId, InquiryCreateRequest request) {
		StudentCouncil writer = studentCouncilRepository
			.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		Inquiry inquiry = inquiryMapper.createInquiry(writer, request);
		Inquiry savedInquiry = inquiryRepository.save(inquiry);

		return inquiryMapper.toInquiryCreateResponse(savedInquiry);
	}

	@Transactional(readOnly = true)
	public Page<InquiryListItemResponse> getMyInquiries(Long councilId, Pageable pageable) {
		StudentCouncil council = studentCouncilRepository
			.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		Page<Inquiry> inquiries = inquiryRepository.findAllByStudentCouncilWriterOrderByCreatedAtDesc(council,
			pageable);

		return inquiries.map(inquiryMapper::toInquiryListItemResponse);
	}
}
