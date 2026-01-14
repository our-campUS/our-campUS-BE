package com.campus.campus.domain.review.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NotPartnershipReceiptException extends ApplicationException {
	public NotPartnershipReceiptException() {
		super(ErrorCode.NOT_PARTNERSHIP_RECEIPT_ERROR);
	}
}
