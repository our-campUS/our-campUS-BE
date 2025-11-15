package com.campus.campus.global.common.exception;

import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {
	private final ErrorCodeInterface errorCode;

	public ApplicationException(final ErrorCodeInterface errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ApplicationException(final ErrorCodeInterface errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
}
