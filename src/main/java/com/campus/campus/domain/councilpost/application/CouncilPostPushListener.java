package com.campus.campus.domain.councilpost.application;

import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.campus.campus.domain.councilpost.application.dto.request.CouncilPostCreatedEvent;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.global.firebase.application.service.FirebaseCloudMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouncilPostPushListener {

	private static final String DATA_KEY_TYPE = "type";
	private static final String DATA_TYPE_COUNCIL_POST_CREATED = "COUNCIL_POST_CREATED";
	private static final String DATA_KEY_POST_ID = "postId";
	private static final String DATA_KEY_CATEGORY = "category";

	private final FirebaseCloudMessageService firebaseCloudMessageService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleCouncilPostCreatedEvent(CouncilPostCreatedEvent event) {

		String title = event.councilName();
		String body = resolveBody(event.category());

		log.info("[PUSH] after_commit event received. topic={}, postId={}, category={}",
			event.topic(), event.postId(), event.category());

		firebaseCloudMessageService.sendToTopic(
			event.topic(),
			title,
			body,
			Map.of(
				DATA_KEY_TYPE, DATA_TYPE_COUNCIL_POST_CREATED,
				DATA_KEY_POST_ID, String.valueOf(event.postId()),
				DATA_KEY_CATEGORY, event.category().name()
			)
		);
	}

	private String resolveBody(PostCategory category) {
		return switch (category) {
			case PARTNERSHIP -> "새 제휴 게시글이 등록되었습니다.";
			case EVENT -> "새 행사글이 등록되었습니다.";
		};
	}
}
