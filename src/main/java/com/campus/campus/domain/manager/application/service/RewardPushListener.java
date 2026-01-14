package com.campus.campus.domain.manager.application.service;

import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.campus.campus.domain.manager.application.dto.request.RewardGrantedEvent;
import com.campus.campus.domain.notification.application.service.NotificationService;
import com.campus.campus.global.firebase.application.service.FirebaseCloudMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardPushListener {
	private static final String DATA_KEY_TYPE = "type";
	private static final String DATA_TYPE_REWARD_GRANTED = "REWARD_GRANTED";

	private final FirebaseCloudMessageService firebaseCloudMessageService;
	private final NotificationService notificationService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRewardGrantedEvent(RewardGrantedEvent rewardGrantedEvent) {
		String title = "보상 지급 알림";
		String body = "스탬프 보상이 지급되었습니다. 보상함을 확인해주세요.";

		notificationService.saveRewardGrantedNotification(rewardGrantedEvent.userId(), title, body);

		String userTopic = "user_" + rewardGrantedEvent.userId();

		log.info("[PUSH] Reward granted. topic={}, userId={}", userTopic, rewardGrantedEvent.userId());

		firebaseCloudMessageService.sendToTopic(
			userTopic,
			title,
			body,
			Map.of(
				DATA_KEY_TYPE, DATA_TYPE_REWARD_GRANTED
			)
		);
	}
}
