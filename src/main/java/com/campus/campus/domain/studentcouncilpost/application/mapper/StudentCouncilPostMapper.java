package com.campus.campus.domain.studentcouncilpost.application.mapper;

import com.campus.campus.domain.studentcouncilpost.application.dto.PostListItemResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostResponseDto;
import com.campus.campus.domain.studentcouncilpost.domain.entity.StudentCouncilPost;
import java.util.Collections;
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
                post.getEndDateTime(),
                post.getThumbnailImageUrl(),
                post.getThumbnailIcon(),
                currentUserId != null && post.getWriter().getId().equals(currentUserId)
        );

    }

    public static PostResponseDto toDetail(
            StudentCouncilPost post,
            List<String> images,
            Long currentUserId
    ) {

        var writer = post.getWriter();
        var builder = PostResponseDto.builder()
                .id(post.getId())
                .writerId(writer.getId())
                .writerName(writer.getFullCouncilName())
                .isWriter(post.isWrittenBy(currentUserId))
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .place(post.getPlace())
                .thumbnailImageUrl(post.getThumbnailImageUrl())
                .thumbnailIcon(post.getThumbnailIcon())
                .images(images != null ? images : Collections.emptyList());

        if (post.isEvent()) {
            builder.startDateTime(post.getStartDateTime());
        } else {
            builder.startDate(post.getDisplayStartDate());
            builder.endDate(post.getDisplayEndDate());
        }

        return builder.build();
    }

}
