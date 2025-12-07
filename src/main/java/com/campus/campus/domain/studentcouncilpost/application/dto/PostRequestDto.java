package com.campus.campus.domain.studentcouncilpost.application.dto;

import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.ThumbnailIcon;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {

    private String title;
    private String content;

    private String place;

    private LocalDate startDate;
    private LocalDate endDate;

    private PostCategory category;

    private String thumbnailImageUrl;

    private ThumbnailIcon thumbnailIcon;
}



