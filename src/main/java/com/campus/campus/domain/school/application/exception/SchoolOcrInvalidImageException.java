package com.campus.campus.domain.school.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class SchoolOcrInvalidImageException extends ApplicationException {
    public SchoolOcrInvalidImageException() {
        super(ErrorCode.SCHOOL_OCR_INVALID_IMAGE);
    }
}
