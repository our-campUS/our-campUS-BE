package com.campus.campus.global.auth.application.dto;

public record AppleTokenClaims(
	String appleId,
	String email
) {
}
