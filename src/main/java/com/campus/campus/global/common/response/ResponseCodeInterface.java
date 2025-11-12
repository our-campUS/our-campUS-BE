package com.campus.campus.global.common.response;

import org.springframework.http.HttpStatus;

public interface ResponseCodeInterface {
	int getCode();

	HttpStatus getStatus();

	String getMessage();
}
