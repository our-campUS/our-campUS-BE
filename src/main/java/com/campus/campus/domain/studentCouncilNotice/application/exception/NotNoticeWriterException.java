package com.campus.campus.domain.studentCouncilNotice.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NotNoticeWriterException extends ApplicationException {
	public NotNoticeWriterException() {
		super(ErrorCode.NOT_NOTICE_WRITER);
	}
}
