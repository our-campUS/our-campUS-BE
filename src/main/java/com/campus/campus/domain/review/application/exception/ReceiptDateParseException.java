package com.campus.campus.domain.review.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class ReceiptDateParseException extends ApplicationException {
	public ReceiptDateParseException() {
		super(ErrorCode.RECEIPT_DATE_PARSE_ERROR);
	}
}
