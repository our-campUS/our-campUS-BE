package com.campus.campus.global.firebase.exception;

public class FirebaseInitializationFailedException extends RuntimeException {
	public FirebaseInitializationFailedException() {
		super(ErrorCode.FIREBASE_INITIALIZATION_FAILED.getMessage());
	}
}
