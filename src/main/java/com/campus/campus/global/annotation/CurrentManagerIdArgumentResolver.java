package com.campus.campus.global.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.campus.campus.global.util.jwt.ManagerPrincipal;
import com.campus.campus.global.util.jwt.exception.UnAuthorizedException;

@Component
public class CurrentManagerIdArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasAnnotation = parameter.hasParameterAnnotation(CurrentManagerId.class);
		boolean hasSupportedType =
			Long.class.equals(parameter.getParameterType()) ||
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
		CurrentManagerId anno = parameter.getParameterAnnotation(CurrentManagerId.class);
		boolean required = anno == null || anno.required();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 1) 인증 자체가 없거나, 인증 안된 경우
		if (authentication == null || !authentication.isAuthenticated()) {
			if (required) {
				throw new UnAuthorizedException();
			}
			return null;
		}

		Object principal = authentication.getPrincipal();

		// 2) JwtAuthenticationFilter 에서 principal 을 ManagerPrincipal 로 넣어뒀음
		if (principal instanceof ManagerPrincipal managerPrincipal) {
			Long id = managerPrincipal.getManagerId();
			if (id == null && required) {
				throw new UnAuthorizedException();
			}
			return id;
		}

		// 3) principal 타입이 예상과 다름 (예: String "anonymousUser" 등)
		if (required) {
			throw new UnAuthorizedException();
		}
		return null;
	}
}
