package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NewPasswordConfirmNotMatchException extends ApplicationException {
  public NewPasswordConfirmNotMatchException() {
    super(ErrorCode.NEW_PASSWORD_CONFIRM_NOT_CORRECT);
  }
}
