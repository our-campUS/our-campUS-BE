package com.campus.campus.global.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean(name = "fcmTaskExecutor")
	public ThreadPoolTaskExecutor fcmTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);

		executor.setQueueCapacity(1000);

		executor.setThreadNamePrefix("fcm-");

		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

		executor.initialize();
		return executor;
	}
}
