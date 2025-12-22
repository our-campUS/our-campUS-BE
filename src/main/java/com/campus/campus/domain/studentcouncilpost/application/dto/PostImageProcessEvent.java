package com.campus.campus.domain.studentcouncilpost.application.dto;

import java.util.List;

public record PostImageProcessEvent(
        Long postId,                // 어느 게시글의 이미지인지
        String tempThumbnailUrl,    // 옮겨야 할 썸네일 임시 경로
        List<String> tempImageUrls  // 옮겨야 할 본문 이미지들의 임시 경로 리스트
) {
}
