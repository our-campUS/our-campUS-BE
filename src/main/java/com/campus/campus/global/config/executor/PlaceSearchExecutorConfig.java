package com.campus.campus.global.config.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlaceSearchExecutorConfig {
	@Bean(destroyMethod = "close")
	public ExecutorService placeSearchExecutor() {
		return Executors.newVirtualThreadPerTaskExecutor();
	}
}
