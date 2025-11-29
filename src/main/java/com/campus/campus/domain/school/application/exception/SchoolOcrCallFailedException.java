package com.campus.campus.domain.school.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class SchoolOcrCallFailedException extends ApplicationException {
    public SchoolOcrCallFailedException() {
        super(ErrorCode.SCHOOL_OCR_CALL_FAILED);
    }
}
