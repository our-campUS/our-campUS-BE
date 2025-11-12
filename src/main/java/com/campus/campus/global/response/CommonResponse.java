package com.campus.campus.global.response;

public record CommonResponse<T>(
	int code,
	String message,
	T data
) {
	public static CommonResponse<Void> success(ResponseCodeInterface responseCode) {
		return new CommonResponse<>(
			responseCode.getCode(),
			responseCode.getMessage(),
			null
		);
	}

	public static <T> CommonResponse<T> success(ResponseCodeInterface responseCode, T data) {
		return new CommonResponse<>(
			responseCode.getCode(),
			responseCode.getMessage(),
			data
		);
	}
}
