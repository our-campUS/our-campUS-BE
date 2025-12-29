package com.campus.campus.domain.studentCouncilNotice.application.exception;

import com.campus.campus.domain.studentCouncilNotice.application.exception.ErrorCode;
import com.campus.campus.global.common.exception.ApplicationException;

public class NoticeImageLimitExceededException extends ApplicationException {
	public NoticeImageLimitExceededException() {
		super(ErrorCode.NOTICE_IMAGE_LIMIT_EXCEEDED);
	}
}
