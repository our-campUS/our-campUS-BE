package com.campus.campus.domain.studentcouncilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PartnershipDateRequiredException extends ApplicationException {
	public PartnershipDateRequiredException() {
		super(ErrorCode.PARTNERSHIP_DATE_REQUIRED);
	}
}
