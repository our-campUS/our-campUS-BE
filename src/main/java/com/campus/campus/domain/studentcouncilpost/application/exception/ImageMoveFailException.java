package com.campus.campus.domain.studentcouncilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class ImageMoveFailException extends ApplicationException {
  public ImageMoveFailException() {
    super(ErrorCode.POST_IMAGE_MOVE_FAILED);
  }
}
