package com.campus.campus.global.firebase.application.service;

import java.util.Map;

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
			log.info("[FCM] sent. topic={}, messageId={}, title={}, body={}, dataKeys={}",
				topic,
				messageId,
				title,
				body,
				(data == null ? "[]" : data.keySet().toString())
			);

		} catch (FirebaseMessagingException e) {
			log.error("[FCM] send failed. topic={}, errorCode={}, message={}",
				topic,
				e.getErrorCode(),
				e.getMessage(),
				e
			);
			throw new FcmTopicSendFailedException(e);
		}
	}
}
