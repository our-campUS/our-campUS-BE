package com.campus.campus.global.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = NicknameValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNickname {
	String message() default "닉네임은 한글, 영문, 숫자만 사용하여 2자 이상 15자 이하로 입력해주세요.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
