package com.campus.campus.domain.councilNotice.application.dto.request;

import java.util.List;

public record NoticeRequestDto(
	String title,
	String content,
	List<String> imageUrls
) {
}
