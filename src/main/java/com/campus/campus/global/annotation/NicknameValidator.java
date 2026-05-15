package com.campus.campus.global.annotation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NicknameValidator implements ConstraintValidator<ValidNickname, String> {

	private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{2,15}$");

	@Override
	public boolean isValid(String nickname, ConstraintValidatorContext context) {
		if (nickname == null) {
			return true;
		}
		return NICKNAME_PATTERN.matcher(nickname).matches();
	}
}
