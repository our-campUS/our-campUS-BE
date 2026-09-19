package com.campus.campus.domain.review.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class InappropriateReviewContentException extends ApplicationException {
  public InappropriateReviewContentException() {
    super(ErrorCode.INAPPROPRIATE_REVIEW_CONTENT);
  }
}
