package com.campus.campus.domain.inquiry.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.inquiry.application.dto.request.InquiryCreateRequest;
import com.campus.campus.domain.inquiry.application.dto.response.InquiryCreateResponse;
import com.campus.campus.domain.inquiry.application.dto.response.InquiryListItemResponse;
import com.campus.campus.domain.inquiry.domain.entity.Inquiry;
import com.campus.campus.domain.inquiry.domain.entity.WriterType;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InquiryMapper {

	public Inquiry createInquiry(User writer, InquiryCreateRequest dto) {
		return Inquiry.builder()
			.writer(writer)
			.writerType(WriterType.USER)
			.title(dto.title())
			.content(dto.content())
			.build();
	}

	public Inquiry createInquiry(StudentCouncil council, InquiryCreateRequest dto) {
		return Inquiry.builder()
			.studentCouncilWriter(council)
			.writerType(WriterType.STUDENT_COUNCIL)
			.title(dto.title())
			.content(dto.content())
			.build();
	}

	public InquiryCreateResponse toInquiryCreateResponse(Inquiry inquiry) {
		return new InquiryCreateResponse(
			inquiry.getId(),
			inquiry.getWriterId(),
			inquiry.getWriterType(),
			inquiry.getTitle(),
			inquiry.getContent(),
			inquiry.getStatus().name(),
			inquiry.getCreatedAt()
		);
	}

	public InquiryListItemResponse toInquiryListItemResponse(Inquiry inquiry) {
		return new InquiryListItemResponse(
			inquiry.getId(),
			inquiry.getWriterId(),
			inquiry.getWriterName(),
			inquiry.getWriterType(),
			inquiry.getTitle(),
			inquiry.getContent(),
			inquiry.getStatus().name(),
			inquiry.getAnswer(),
			inquiry.getCreatedAt(),
			inquiry.getAnsweredAt()
		);
	}
}
