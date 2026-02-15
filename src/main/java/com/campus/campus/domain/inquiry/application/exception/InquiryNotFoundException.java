package com.campus.campus.domain.inquiry.application.exception;

import com.campus.campus.domain.user.application.exception.ErrorCode;
import com.campus.campus.global.common.exception.ApplicationException;

public class InquiryNotFoundException extends ApplicationException {
	public InquiryNotFoundException() {
		super(ErrorCode.INQUIRY_NOT_FOUND);
	}
}
