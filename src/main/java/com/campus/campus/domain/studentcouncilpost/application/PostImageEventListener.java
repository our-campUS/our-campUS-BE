package com.campus.campus.domain.studentcouncilpost.application;

import com.campus.campus.domain.studentcouncilpost.application.dto.PostImageProcessEvent;
import com.campus.campus.global.oci.OciPresignedUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostImageEventListener {

    private final OciPresignedUrlService ociService;

    @Async("imageExecutor") // 설정해둔 전용 쓰레드 풀 사용
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageFinalize(PostImageProcessEvent event) {
        log.info(">>> [ASYNC] OCI 이미지 이동 시작 - Post ID: {}", event.postId());

        try {
            // 1. 썸네일 이동
            if (event.tempThumbnailUrl() != null && event.tempThumbnailUrl().contains("/temp/")) {
                ociService.copyTempToFinal(event.tempThumbnailUrl(), event.postId());
            }

            // 2. 본문 이미지들 이동
            if (event.tempImageUrls() != null) {
                for (String tempUrl : event.tempImageUrls()) {
                    if (tempUrl.contains("/temp/")) {
                        ociService.copyTempToFinal(tempUrl, event.postId());
                    }
                }
            }
            log.info(">>> [ASYNC] OCI 이미지 이동 완료 - Post ID: {}", event.postId());

        } catch (Exception e) {
            // 비동기 작업이므로 여기서 에러가 터져도 사용자는 이미 성공 응답을 받은 상태임
            log.error(">>> [ASYNC ERROR] 이미지 처리 중 오류 발생 (Post ID: {}): {}",
                    event.postId(), e.getMessage(), e);
            // 필요시 에러 테이블 저장 로직 추가
        }
    }
}