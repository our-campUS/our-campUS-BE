package com.campus.campus.global.auth.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.global.util.jwt.exception.UnAuthorizedException;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// @CurrentUserId 가 붙어 있고 타입이 long/Long 인 파라미터에만 동작
		boolean hasAnnotation = parameter.hasParameterAnnotation(CurrentUserId.class);
		boolean hasSupportedType =
			Long.class.isAssignableFrom(parameter.getParameterType()) ||
				long.class.equals(parameter.getParameterType());

		return hasAnnotation && hasSupportedType;
	}

	@Override
	public Object resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory
	) {
		CurrentUserId currentUserId = parameter.getParameterAnnotation(CurrentUserId.class);
		boolean required = currentUserId == null || currentUserId.required();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 인증 정보가 없거나, 익명 사용자면
		if (authentication == null || !authentication.isAuthenticated()
			|| "anonymousUser".equals(authentication.getPrincipal())) {

			if (required) {
				throw new UnAuthorizedException();
			}
			return null; // required=false 이면 null 주입
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof User user) {
			return user.getId();
		}

		if (required) {
			throw new UnAuthorizedException();
		}
		return null;
	}
}