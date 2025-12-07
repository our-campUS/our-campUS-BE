package com.campus.campus.domain.studentcouncilpost.application.dto;

import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.ThumbnailIcon;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponseDto {
    private Long id;
    private Long writerId;
    private PostCategory category;
    private String title;
    private String content;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private String thumbnailImageUrl;
    private ThumbnailIcon thumbnailIcon;
}
