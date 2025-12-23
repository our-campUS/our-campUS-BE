package com.campus.campus.domain.studentcouncilpost.application.mapper;

import com.campus.campus.domain.studentcouncilpost.application.dto.PostListItemResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostResponseDto;
import com.campus.campus.domain.studentcouncilpost.domain.entity.StudentCouncilPost;
import java.util.List;

public class StudentCouncilPostMapper {

    public static PostListItemResponseDto toListItem(
            StudentCouncilPost post,
            Long currentUserId
    ) {
        return new PostListItemResponseDto(
                post.getId(),
                post.getCategory(),
                post.getTitle(),
                post.getPlace(),
                post.getEndDate(),
                post.getThumbnailImageUrl(),
                post.getThumbnailIcon(),
                currentUserId != null && post.getWriter().getId().equals(currentUserId)
        );

    }

    public static PostResponseDto toDetail(
            StudentCouncilPost post,
            List<String> finalImages,
            Long currentUserId
    ) {
        var writer = post.getWriter();

        return PostResponseDto.builder()
                .id(post.getId())
                .writerId(writer.getId())
                .writerName(writer.getFullCouncilName())
                .isWriter(writer.getId().equals(currentUserId))
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .place(post.getPlace())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .thumbnailImageUrl(post.getThumbnailImageUrl())
                .thumbnailIcon(post.getThumbnailIcon())
                .images(finalImages)
                .build();
    }
}
