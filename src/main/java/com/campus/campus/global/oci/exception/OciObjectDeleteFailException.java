package com.campus.campus.global.oci.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class OciObjectDeleteFailException extends ApplicationException {
	public OciObjectDeleteFailException() {
		super(ErrorCode.OCI_OBJECT_DELETE_FAILED);
	}
}
