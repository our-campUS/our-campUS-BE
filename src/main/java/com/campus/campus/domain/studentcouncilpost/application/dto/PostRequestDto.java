package com.campus.campus.domain.studentcouncilpost.application.dto;

import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.ThumbnailIcon;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record PostRequestDto(

        @NotNull
        PostCategory category,

        @NotBlank
        String title,

        @NotBlank
        String content,

        String place,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        // 썸네일 (둘 중 하나는 필수)
        String thumbnailImageUrl,
        ThumbnailIcon thumbnailIcon,

        // 본문 이미지들
        List<String> imageUrls
) {}