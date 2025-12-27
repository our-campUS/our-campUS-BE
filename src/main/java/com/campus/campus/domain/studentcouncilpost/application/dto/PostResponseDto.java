package com.campus.campus.domain.studentcouncilpost.application.dto;

import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.ThumbnailIcon;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostResponseDto (

        Long id,
        Long writerId,
        String writerName,
        Boolean isWriter,

        PostCategory category,
        String title,
        String content,
        String place,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime startDateTime,

        String thumbnailImageUrl,
        ThumbnailIcon thumbnailIcon,

        List<String> images
) {}
