package com.campus.campus.domain.review.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class ReceiptFileConvertException extends ApplicationException {
	public ReceiptFileConvertException() {
		super(ErrorCode.RECEIPT_FILE_CONVERT_ERROR);
	}
}