package com.campus.campus.domain.studentcouncilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class EventStartDateTimeRequiredException extends ApplicationException {
  public EventStartDateTimeRequiredException() {
    super(ErrorCode.EVENT_START_DATETIME_REQUIRED);
  }
}
