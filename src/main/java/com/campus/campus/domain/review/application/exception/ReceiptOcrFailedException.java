package com.campus.campus.domain.review.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class ReceiptOcrFailedException extends ApplicationException {
	public ReceiptOcrFailedException() {
		super(ErrorCode.RECEIPT_OCR_FAILED);
	}
}
