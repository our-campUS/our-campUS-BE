package com.campus.campus.domain.notification.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NotificationNotFoundException extends ApplicationException {
	public NotificationNotFoundException() {
		super(ErrorCode.NOTIFICATION_NOT_FOUND);
	}
}
