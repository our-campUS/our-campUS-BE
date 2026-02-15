package com.campus.campus.domain.inquiry.application.exception;

import com.campus.campus.domain.user.application.exception.ErrorCode;
import com.campus.campus.global.common.exception.ApplicationException;

public class AlreadyAnsweredInquiry extends ApplicationException {
	public AlreadyAnsweredInquiry() {
		super(ErrorCode.ALREADY_ANSWERED_INQUIRY);
	}
}
