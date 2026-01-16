package com.campus.campus.domain.review.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class ReceiptImageFormatException extends ApplicationException {
	public ReceiptImageFormatException() {
		super(ErrorCode.RECEIPT_FILE_TYPE_ERROR);
	}
}