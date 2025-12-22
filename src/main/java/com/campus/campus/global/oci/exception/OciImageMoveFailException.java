package com.campus.campus.global.oci.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class OciImageMoveFailException extends ApplicationException {
    public OciImageMoveFailException() {
        super(ErrorCode.OCI_IMAGE_MOVE_FAILED);
    }
}
