package com.campus.campus.domain.studentCouncilNotice.application.mapper;

import java.util.List;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.studentCouncilNotice.application.dto.request.NoticeRequestDto;
import com.campus.campus.domain.studentCouncilNotice.application.dto.response.NoticeListItemResponseDto;
import com.campus.campus.domain.studentCouncilNotice.application.dto.response.NoticeResponseDto;
import com.campus.campus.domain.studentCouncilNotice.domain.entity.NoticeImage;
import com.campus.campus.domain.studentCouncilNotice.domain.entity.StudentCouncilNotice;

public class StudentCouncilNoticeMapper {

	public static StudentCouncilNotice toEntity(StudentCouncil writer, NoticeRequestDto dto){
		return StudentCouncilNotice.builder()
			.title(dto.title())
			.content(dto.content())
			.writer(writer)
			.build();
	}

	public static NoticeImage toEntity(StudentCouncilNotice notice, String imageUrl) {
		return NoticeImage.builder()
			.notice(notice)
			.imageUrl(imageUrl)
			.build();
	}

	public static NoticeResponseDto toDetail(StudentCouncilNotice notice, List<String> imageUrls, Long councilId) {
		return NoticeResponseDto.builder()
			.id(notice.getId())
			.writerId(notice.getWriter().getId())
			.writerName(notice.getWriter().getFullCouncilName())
			.isWriter(notice.isWrittenByCouncil(councilId))
			.title(notice.getTitle())
			.content(notice.getContent())
			.images(imageUrls)
			.build();
	}

	public static NoticeListItemResponseDto toListItem(StudentCouncilNotice notice, Long currentUserId) {
		return NoticeListItemResponseDto.builder()
			.id(notice.getId())
			.title(notice.getTitle())
			.isWriter(notice.isWrittenByCouncil(currentUserId))
			.createdAt(notice.getCreatedAt())
			.build();
	}
}
