package com.campus.campus.domain.councilnotice.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.councilnotice.application.dto.request.NoticeRequest;
import com.campus.campus.domain.councilnotice.application.dto.response.NoticeListItemResponse;
import com.campus.campus.domain.councilnotice.application.dto.response.NoticeResponse;
import com.campus.campus.domain.councilnotice.domain.entity.NoticeImage;
import com.campus.campus.domain.councilnotice.domain.entity.StudentCouncilNotice;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudentCouncilNoticeMapper {

	public StudentCouncilNotice createStudentCouncilNotice(StudentCouncil writer, NoticeRequest dto) {
		return StudentCouncilNotice.builder()
			.title(dto.title())
			.content(dto.content())
			.writer(writer)
			.build();
	}

	public NoticeImage createStudentCouncilNoticeImage(StudentCouncilNotice notice, String imageUrl) {
		return NoticeImage.builder()
			.notice(notice)
			.imageUrl(imageUrl)
			.build();
	}

	public NoticeResponse toNoticeResponse(StudentCouncilNotice notice, List<String> imageUrls, Long councilId) {
		return NoticeResponse.builder()
			.id(notice.getId())
			.writerId(notice.getWriter().getId())
			.writerName(notice.getWriter().getFullCouncilName())
			.isWriter(notice.isWrittenByCouncil(councilId))
			.title(notice.getTitle())
			.content(notice.getContent())
			.images(imageUrls)
			.createdAt(notice.getCreatedAt())
			.updatedAt(notice.getUpdatedAt())
			.build();
	}

	public NoticeListItemResponse toNoticeListItemResponse(StudentCouncilNotice notice, Long currentUserId) {
		return NoticeListItemResponse.builder()
			.id(notice.getId())
			.title(notice.getTitle())
			.isWriter(notice.isWrittenByCouncil(currentUserId))
			.createdAt(notice.getCreatedAt())
			.updatedAt(notice.getUpdatedAt())
			.build();
	}
}
