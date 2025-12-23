package com.campus.campus.domain.studentcouncilpost.application.dto;

import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.ThumbnailIcon;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponseDto {
    private Long id;
    private Long writerId;
    private String writerName;
    private Boolean isWriter;

    private PostCategory category;
    private String title;
    private String content;

    private String place;
    private LocalDate startDate;
    private LocalDate endDate;

    private String thumbnailImageUrl;
    private ThumbnailIcon thumbnailIcon;

    private List<String> images;
}
