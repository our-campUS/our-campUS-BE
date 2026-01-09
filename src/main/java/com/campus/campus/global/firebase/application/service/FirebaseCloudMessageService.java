package com.campus.campus.global.firebase.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.campus.campus.domain.user.application.service.UserService;
import com.campus.campus.global.firebase.application.dto.FcmMessageRequestDto;
import com.campus.campus.global.firebase.exception.FcmTopicSendFailedException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;

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
			FirebaseMessaging.getInstance().send(builder.build());
		} catch (FirebaseMessagingException e) {
			throw new FcmTopicSendFailedException(e);
		}
	}
}
