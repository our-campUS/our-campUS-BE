package com.campus.campus.global.oci.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class OciPresignedUrlCreateFailException extends ApplicationException {
	public OciPresignedUrlCreateFailException() {
		super(ErrorCode.OCI_PRESIGNED_URL_CREATE_FAILED);
	}
}
