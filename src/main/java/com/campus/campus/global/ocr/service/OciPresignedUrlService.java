package com.campus.campus.global.ocr.service;

import com.campus.campus.global.common.exception.OciPresignedUrlCreateFailException;
import com.campus.campus.global.config.OciConfig;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OciPresignedUrlService {

    private final ObjectStorage objectStorage;
    private final OciConfig ociConfig;

    public String createPresignedPutUrl(String objectName) {
        try {

            // =======================
            // 1) 요청 파라미터 로그
            // =======================
            log.info(">>> [PRESIGNED] region={}", ociConfig.getRegion());
            log.info(">>> [PRESIGNED] namespace={}", ociConfig.getNamespace());
            log.info(">>> [PRESIGNED] bucket={}", ociConfig.getBucketName());
            log.info(">>> [PRESIGNED] objectName={}", objectName);

            String ociObjectName = objectName;

            // =======================
            // 2) PAR 요청 객체 생성 로그
            // =======================
            CreatePreauthenticatedRequestDetails details =
                    CreatePreauthenticatedRequestDetails.builder()
                            .name("upload-" + ociObjectName)
                            .objectName(ociObjectName)
                            .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite)
                            .timeExpires(new Date(System.currentTimeMillis() + 1000 * 60 * 10))  // 10분
                            .build();

            CreatePreauthenticatedRequestRequest request =
                    CreatePreauthenticatedRequestRequest.builder()
                            .bucketName(ociConfig.getBucketName())
                            .namespaceName(ociConfig.getNamespace())
                            .createPreauthenticatedRequestDetails(details)
                            .build();

            log.info(">>> [PRESIGNED] request details = {}", details);
            log.info(">>> [PRESIGNED] request = {}", request);

            // =======================
            // 3) OCI 호출
            // =======================
            CreatePreauthenticatedRequestResponse response =
                    objectStorage.createPreauthenticatedRequest(request);

            log.info(">>> [PRESIGNED] response = {}", response.getPreauthenticatedRequest());

            // =======================
            // 4) URL 생성
            // =======================
            return String.format(
                    "https://objectstorage.%s.oraclecloud.com%s",
                    ociConfig.getRegion(),
                    response.getPreauthenticatedRequest().getAccessUri()
            );

        } catch (Exception e) {

            // =======================
            // 5) 실제 OCI 에러 메시지 출력
            // =======================
            log.error(">>> [PRESIGNED ERROR] {}", e.getMessage(), e);

            // 원인 전달 (개발용)
            throw new OciPresignedUrlCreateFailException(
                    "OCI Presigned URL 생성 실패: " + e.getMessage()
            );
        }
    }
}

