package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class Sha256AlgorithmException extends ApplicationException {
	public Sha256AlgorithmException() {
		super(ErrorCode.SHA256_NOT_SUPPORTED);
	}
}
