package com.campus.campus.domain.school.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class SchoolOcrParseFailedException extends ApplicationException {
    public SchoolOcrParseFailedException() {
        super(ErrorCode.SCHOOL_OCR_PARSE_FAILED);
    }
}
