package com.campus.campus.global.common.exception;

public class OciPresignedUrlCreateFailException extends ApplicationException {

  public OciPresignedUrlCreateFailException(String s) {
    super(OciErrorCode.OCI_PRESIGNED_URL_CREATE_FAILED);
  }
}
