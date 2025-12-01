package com.campus.campus.global.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StorageErrorCode implements ErrorCodeInterface {


    // 6100번대: Storage 연결/인증 에러
    STORAGE_CONNECTION_FAILED(6101, HttpStatus.INTERNAL_SERVER_ERROR, "스토리지 연결에 실패했습니다."),
    STORAGE_TIMEOUT(6102, HttpStatus.GATEWAY_TIMEOUT, "스토리지 요청 시간이 초과되었습니다."),
    STORAGE_AUTHENTICATION_FAILED(6103, HttpStatus.UNAUTHORIZED, "스토리지 인증에 실패했습니다."),
    STORAGE_CONFIG_NOT_FOUND(6104, HttpStatus.INTERNAL_SERVER_ERROR, "스토리지 설정 파일을 찾을 수 없습니다."),
    STORAGE_INVALID_CREDENTIALS(6105, HttpStatus.UNAUTHORIZED, "스토리지 인증 정보가 올바르지 않습니다."),

    // 6200번대: Bucket 에러
    BUCKET_NOT_FOUND(6201, HttpStatus.NOT_FOUND, "버킷을 찾을 수 없습니다."),
    BUCKET_ACCESS_DENIED(6202, HttpStatus.FORBIDDEN, "버킷 접근 권한이 없습니다."),
    BUCKET_ALREADY_EXISTS(6203, HttpStatus.CONFLICT, "이미 존재하는 버킷입니다."),

    // 6300번대: Object 에러
    OBJECT_NOT_FOUND(6301, HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    OBJECT_ALREADY_EXISTS(6302, HttpStatus.CONFLICT, "이미 존재하는 파일입니다."),
    OBJECT_TOO_LARGE(6303, HttpStatus.BAD_REQUEST, "파일 크기가 너무 큽니다. (최대 50MB)"),
    INVALID_FILE_NAME(6304, HttpStatus.BAD_REQUEST, "올바르지 않은 파일명입니다."),

    // 6400번대: Upload/Download 에러
    UPLOAD_FAILED(6401, HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    DOWNLOAD_FAILED(6402, HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다."),
    DELETE_FAILED(6403, HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    FILE_READ_ERROR(6404, HttpStatus.INTERNAL_SERVER_ERROR, "파일 읽기 중 오류가 발생했습니다."),
    PRESIGNED_URL_CREATE_FAILED(6405, HttpStatus.INTERNAL_SERVER_ERROR, "Presigned URL 생성에 실패했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
