package com.campus.campus.global.firebase.exception;

public class FcmTopicSendFailedException extends RuntimeException {
	public FcmTopicSendFailedException(Throwable cause) {
		super(ErrorCode.FCM_TOPIC_SEND_FAILED.getMessage(), cause);
	}
}
