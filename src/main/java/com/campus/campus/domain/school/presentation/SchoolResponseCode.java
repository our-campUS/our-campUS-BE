package com.campus.campus.domain.school.presentation;
import com.campus.campus.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SchoolResponseCode implements ResponseCodeInterface {

    OCR_STUDENT_CARD_SUCCESS(200, HttpStatus.OK, "학생증 OCR에 성공했습니다."),
    OCR_STUDENT_COUNCIL_SUCCESS(200, HttpStatus.OK, "당선 정보 OCR에 성공했습니다."),
    OCR_PRESIGNED_URL_SUCCESS(200, HttpStatus.OK, "OCI Presigned URL 발급 성공했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
