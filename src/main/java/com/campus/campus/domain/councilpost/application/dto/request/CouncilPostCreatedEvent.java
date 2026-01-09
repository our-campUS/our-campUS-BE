package com.campus.campus.domain.councilpost.application.dto.request;

public record CouncilPostCreatedEvent(
	Long postId,
	String councilName,
	String category,
	String topic
) {}
