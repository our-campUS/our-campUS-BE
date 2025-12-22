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

    // 썸네일: 둘 중 하나만 내려주면 UX 처리 편함
    private String thumbnailImageUrl;
    private ThumbnailIcon thumbnailIcon;
}
