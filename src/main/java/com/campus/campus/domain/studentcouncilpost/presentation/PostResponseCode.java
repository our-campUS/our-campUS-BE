package com.campus.campus.domain.studentcouncilpost.presentation;

import com.campus.campus.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostResponseCode implements ResponseCodeInterface {

    POST_CREATE_SUCCESS(201, HttpStatus.CREATED, "게시글 생성에 성공했습니다."),
    POST_READ_SUCCESS(200, HttpStatus.OK, "게시글 조회에 성공했습니다."),
    POST_UPDATE_SUCCESS(200, HttpStatus.OK, "게시글 수정에 성공했습니다."),
    POST_DELETE_SUCCESS(204, HttpStatus.NO_CONTENT, "게시글 삭제에 성공했습니다."),

    // 예외 케이스
    POST_NOT_FOUND(404, HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    NOT_POST_WRITER(403, HttpStatus.FORBIDDEN, "작성자만 해당 작업을 수행할 수 있습니다."),
    THUMBNAIL_REQUIRED(400, HttpStatus.BAD_REQUEST, "썸네일(이미지 또는 아이콘)은 반드시 필요합니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
