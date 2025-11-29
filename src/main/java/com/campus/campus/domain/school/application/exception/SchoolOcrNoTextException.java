package com.campus.campus.domain.school.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class SchoolOcrNoTextException extends ApplicationException {
    public SchoolOcrNoTextException() {
        super(ErrorCode.SCHOOL_OCR_NO_TEXT);
    }
}