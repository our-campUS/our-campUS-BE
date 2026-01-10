package com.campus.campus.global.firebase.application.service;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.campus.campus.global.firebase.exception.FcmTopicSendFailedException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

	private final ThreadPoolTaskExecutor fcmTaskExecutor;

	public void sendToTopic(String topic, String title, String body, Map<String, String> data) {
		Message.Builder builder = Message.builder()
			.setTopic(topic)
			.setNotification(Notification.builder()
				.setTitle(title)
				.setBody(body)
				.build());

		if (data != null) {
			data.forEach(builder::putData);
		}

		try {
			String messageId = FirebaseMessaging.getInstance().send(builder.build());

			log.info("[FCM] sent. topic={}, messageId={}, title={}, body={}, dataKeys={}, exec={}",
				topic,
				messageId,
				title,
				body,
				(data == null ? "[]" : data.keySet().toString()),
				execSnapshot()
			);

		} catch (FirebaseMessagingException e) {
			log.error("[FCM] send failed. topic={}, errorCode={}, message={}, exec{}",
				topic,
				e.getErrorCode(),
				e.getMessage(),
				execSnapshot(),
				e
			);
			throw new FcmTopicSendFailedException(e);
		}
	}

	//각각 스레드풀 로그 보기 위한 모니터링 메서드
	private String execSnapshot() {
		ThreadPoolExecutor tp = fcmTaskExecutor.getThreadPoolExecutor();

		return "pool=" + tp.getPoolSize() + "/" + tp.getMaximumPoolSize()
			+ ",active=" + tp.getActiveCount()
			+ ",queue=" + tp.getQueue().size()
			+ ",remain=" + tp.getQueue().remainingCapacity();
	}
}
