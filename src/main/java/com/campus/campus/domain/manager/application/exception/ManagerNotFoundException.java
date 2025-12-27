package com.campus.campus.domain.manager.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class ManagerNotFoundException extends ApplicationException {
	public ManagerNotFoundException() {
		super(ErrorCode.MANAGER_NOT_FOUND);
	}
}
