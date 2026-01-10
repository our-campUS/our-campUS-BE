package com.campus.campus.domain.councilpost.application.dto.request;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;

public record CouncilPostCreatedEvent(
	Long postId,
	String councilName,
	PostCategory category,
	String topic
) {}
