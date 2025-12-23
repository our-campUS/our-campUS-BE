package com.campus.campus.domain.studentcouncilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class EventEndDateTimeNotAllowedException extends ApplicationException {
    public EventEndDateTimeNotAllowedException() {
        super(ErrorCode.EVENT_END_DATETIME_NOT_ALLOWED);
    }
}
