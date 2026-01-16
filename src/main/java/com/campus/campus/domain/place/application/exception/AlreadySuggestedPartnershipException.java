package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class AlreadySuggestedPartnershipException extends ApplicationException {
	public AlreadySuggestedPartnershipException() {
		super(ErrorCode.ALREADY_PARTNERSHIP_SUGGESTED);
	}
}