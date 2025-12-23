package com.campus.campus.global.oci.exception;

public class InvalidImageContentTypeException extends RuntimeException {
    public InvalidImageContentTypeException(String contentType) {
        super("지원하지 않는 이미지 타입입니다: " + contentType);
    }
}
