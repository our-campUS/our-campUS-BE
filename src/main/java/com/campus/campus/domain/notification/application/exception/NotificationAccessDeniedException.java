package com.campus.campus.domain.notification.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NotificationAccessDeniedException extends ApplicationException {
	public NotificationAccessDeniedException() {
		super(ErrorCode.NOTIFICATION_ACCESS_DENIED);
	}
}
