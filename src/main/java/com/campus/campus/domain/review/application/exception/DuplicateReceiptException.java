package com.campus.campus.domain.review.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class DuplicateReceiptException extends ApplicationException {
	public DuplicateReceiptException() {
		super(ErrorCode.DUPLICATE_RECEIPT_ERROR);
	}
}
