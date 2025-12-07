package com.campus.campus.domain.studentcouncilpost.application.mapper;

import com.campus.campus.domain.studentcouncilpost.application.dto.PostResponseDto;
import com.campus.campus.domain.studentcouncilpost.domain.entity.StudentCouncilPost;

public class StudentCouncilPostMapper {

    public static PostResponseDto toDto(StudentCouncilPost post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .writerId(post.getWriter().getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .place(post.getPlace())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .thumbnailImageUrl(post.getThumbnailImageUrl())
                .thumbnailIcon(post.getThumbnailIcon())
                .build();
    }
}
