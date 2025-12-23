package com.campus.campus.global.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password == null) {
			return false;
		}

		int passedCount = 0;

		if (password.matches(".*[A-Z].*")) {
			passedCount++;
		}
		if (password.matches(".*[a-z].*")) {
			passedCount++;
		}
		if (password.matches(".*[0-9].*")) {
			passedCount++;
		}
		if (password.matches(".*[^a-zA-Z0-9].*")) {
			passedCount++;
		}

		return passedCount >= 2;
	}
}
