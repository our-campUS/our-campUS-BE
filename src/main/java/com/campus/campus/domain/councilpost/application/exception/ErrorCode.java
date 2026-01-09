package com.campus.campus.domain.councilpost.application.exception;

import com.campus.campus.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

    POST_NOT_FOUND(2401, HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    NOT_POST_WRITER(2402, HttpStatus.FORBIDDEN, "작성자만 해당 작업을 수행할 수 있습니다."),
    THUMBNAIL_REQUIRED(2403, HttpStatus.BAD_REQUEST, "썸네일(이미지 또는 아이콘)은 반드시 필요합니다."),
    POST_IMAGE_LIMIT_EXCEEDED(2404, HttpStatus.BAD_REQUEST, "게시글 이미지는 최대 10개까지 등록할 수 있습니다."),
    POST_OCI_IMAGE_DELETE_FAILED(2405, HttpStatus.INTERNAL_SERVER_ERROR, "OCI 이미지 삭제에 실패했습니다."),

    // EVENT 관련
    EVENT_START_DATETIME_REQUIRED(2406, HttpStatus.BAD_REQUEST, "행사는 시작 일시가 필요합니다."),
    EVENT_END_DATETIME_NOT_ALLOWED(2407, HttpStatus.BAD_REQUEST, "행사는 종료 일시를 가질 수 없습니다."),

    // PARTNERSHIP 관련
    PARTNERSHIP_DATE_REQUIRED(2408, HttpStatus.BAD_REQUEST, "제휴는 시작일과 종료일이 필요합니다."),

    // 학생의 학생회 게시글 조회 관련
    POST_ACCESS_DENIED(2409, HttpStatus.FORBIDDEN, "해당 게시글에 접근할 권한이 없습니다."),
    COLLEGE_NOT_SET(2410, HttpStatus.BAD_REQUEST, "단과대 정보가 설정되지 않았습니다."),
    MAJOR_NOT_SET(2411, HttpStatus.BAD_REQUEST, "학과 정보가 설정되지 않았습니다.");


    private final int code;
    private final HttpStatus status;
    private final String message;
}
