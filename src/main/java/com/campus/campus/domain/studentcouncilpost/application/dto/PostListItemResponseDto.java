package com.campus.campus.domain.studentcouncilpost.application.dto;

import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.ThumbnailIcon;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostListItemResponseDto {

    private Long id;
    private PostCategory category;

    private String title;
    private String place;
    private LocalDate endDate;

    private String thumbnailImageUrl;
    private ThumbnailIcon thumbnailIcon;

    private Boolean isWriter;
}
