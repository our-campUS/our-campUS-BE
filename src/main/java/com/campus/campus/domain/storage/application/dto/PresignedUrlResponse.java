package com.campus.campus.domain.storage.application.dto;

public record PresignedUrlResponse(
        Long id,
        String objectName,
        String uploadUrl
) {}

