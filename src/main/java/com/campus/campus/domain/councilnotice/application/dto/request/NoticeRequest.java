package com.campus.campus.domain.councilnotice.application.dto.request;

import java.util.List;

public record NoticeRequest(
	String title,
	String content,
	List<String> imageUrls
) {
}
