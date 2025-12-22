package com.campus.campus.global.oci.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class OciObjectCopyFailException extends ApplicationException {
    public OciObjectCopyFailException() {
        super(ErrorCode.OCI_OBJECT_COPY_FAILED);
    }
}
