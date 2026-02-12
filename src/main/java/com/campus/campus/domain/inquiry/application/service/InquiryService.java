package com.campus.campus.domain.inquiry.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.inquiry.application.dto.request.InquiryCreateRequest;
import com.campus.campus.domain.inquiry.application.dto.response.InquiryCreateResponse;
import com.campus.campus.domain.inquiry.application.mapper.InquiryMapper;
import com.campus.campus.domain.inquiry.domain.entity.Inquiry;
import com.campus.campus.domain.inquiry.domain.repository.InquiryRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {
	private final InquiryRepository inquiryRepository;
	private final InquiryMapper inquiryMapper;
	private final UserRepository userRepository;

	public InquiryCreateResponse createInquiry(Long userId, InquiryCreateRequest request) {
		User writer = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		Inquiry inquiry = inquiryMapper.createInquiry(writer, request);
		Inquiry savedInquiry = inquiryRepository.save(inquiry);

		return inquiryMapper.toInquiryCreateResponse(savedInquiry);
	}
}
