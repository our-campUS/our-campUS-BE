package com.campus.campus.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.campus.campus.global.annotation.CurrentCouncilIdArgumentResolver;
import com.campus.campus.global.annotation.CurrentUserIdArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final CurrentUserIdArgumentResolver currentUserIdArgumentResolver;
	private final CurrentCouncilIdArgumentResolver currentCouncilIdArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(currentUserIdArgumentResolver);
		resolvers.add(currentCouncilIdArgumentResolver);
	}
}
