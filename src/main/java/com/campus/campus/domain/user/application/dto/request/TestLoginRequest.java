package com.campus.campus.domain.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TestLoginRequest(
	@NotBlank(message = "이메일")
	String email
) {}
