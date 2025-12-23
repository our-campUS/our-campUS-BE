package com.campus.campus.domain.studentcouncilpost.application.exception;

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

    // EVENT 관련
    EVENT_START_DATETIME_REQUIRED(2405, HttpStatus.BAD_REQUEST, "행사는 시작 일시가 필요합니다."),
    EVENT_END_DATETIME_NOT_ALLOWED(2406, HttpStatus.BAD_REQUEST, "행사는 종료 일시를 가질 수 없습니다."),

    // PARTNERSHIP 관련
    PARTNERSHIP_DATE_REQUIRED(2407, HttpStatus.BAD_REQUEST, "제휴는 시작일과 종료일이 필요합니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
