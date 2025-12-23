package com.campus.campus.domain.studentcouncilpost.application.dto;

import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.ThumbnailIcon;
import java.time.LocalDate;

public record PostListItemResponseDto(
        Long id,
        PostCategory category,
        String title,
        String place,
        LocalDate endDate,
        String thumbnailImageUrl,
        ThumbnailIcon thumbnailIcon,
        Boolean isWriter
) {}