package com.campus.campus.global.oci;

import com.campus.campus.global.config.OciConfig;
import com.campus.campus.global.oci.exception.OciImageMoveFailException;
import com.campus.campus.global.oci.exception.OciObjectCopyFailException;
import com.campus.campus.global.oci.exception.OciObjectDeleteFailException;
import com.campus.campus.global.oci.exception.OciPresignedUrlCreateFailException;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CopyObjectDetails;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CopyObjectRequest;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OciPresignedUrlService {

    private final ObjectStorage objectStorage;
    private final OciConfig ociConfig;

    /**
     * Presigned URL 생성
     */
    public String createPresignedPutUrl(String objectName) {
        try {
            CreatePreauthenticatedRequestDetails details =
                    CreatePreauthenticatedRequestDetails.builder()
                            .name("upload-" + objectName)
                            .objectName(objectName)
                            .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite)
                            .timeExpires(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                            .build();

            CreatePreauthenticatedRequestRequest request =
                    CreatePreauthenticatedRequestRequest.builder()
                            .bucketName(ociConfig.getBucketName())
                            .namespaceName(ociConfig.getNamespace())
                            .createPreauthenticatedRequestDetails(details)
                            .build();

            CreatePreauthenticatedRequestResponse response =
                    objectStorage.createPreauthenticatedRequest(request);

            return String.format(
                    "https://objectstorage.%s.oraclecloud.com%s",
                    ociConfig.getRegion(),
                    response.getPreauthenticatedRequest().getAccessUri()
            );

        } catch (Exception e) {
            log.error(">>> PRESIGNED ERROR: {}", e.getMessage(), e);
            throw new OciPresignedUrlCreateFailException();
        }
    }

    /**
     * temp -> final 이동
     */
    public String moveTempToFinal(String tempUrl, Long postId) {

        String tempObjectName = extractObjectNameFromUrl(tempUrl);

        if (!tempObjectName.startsWith("temp/")) {
            throw new IllegalArgumentException("Temp 이미지가 아닙니다: " + tempUrl);
        }

        String fileName = tempObjectName.substring("temp/".length());
        String finalObjectName = "posts/" + postId + "/" + fileName;

        try {
            copyObject(tempObjectName, finalObjectName);
            deleteObject(tempObjectName);
        } catch (OciObjectCopyFailException | OciObjectDeleteFailException e) {
            throw e;
        } catch (Exception e) {
            log.error(">>> MOVE ERROR: {}", e.getMessage(), e);
            throw new OciImageMoveFailException();
        }

        return ociConfig.fullObjectUrl(finalObjectName);
    }
    /**
     * 이미지 삭제 (URL 기반)
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            String objectName = extractObjectNameFromUrl(imageUrl);
            deleteObject(objectName);
            log.info("Deleted image: {}", objectName);
        } catch (Exception e) {
            log.error(">>> DELETE IMAGE ERROR: {}", e.getMessage(), e);
        }
    }
    public String copyTempToFinal(String tempUrl, Long postId) {
        String tempObjectName = extractObjectNameFromUrl(tempUrl);

        // 이미 final 경로라면 복사할 필요 없음
        if (!tempObjectName.startsWith("temp/")) {
            return tempUrl;
        }

        String fileName = tempObjectName.substring("temp/".length());
        String finalObjectName = "posts/" + postId + "/" + fileName;

        try {
            // OCI 내부 복사 실행
            copyObject(tempObjectName, finalObjectName);
            log.info("OCI Copy Success: {} -> {}", tempObjectName, finalObjectName);
        } catch (Exception e) {
            log.error(">>> ASYNC COPY ERROR: {}", e.getMessage());
            throw new OciImageMoveFailException();
        }

        return ociConfig.fullObjectUrl(finalObjectName);
    }
    /**
     * presignedUrl → objectName 추출
     */
    private String extractObjectNameFromUrl(String url) {
        int idx = url.indexOf("/o/");
        if (idx == -1) throw new IllegalArgumentException("올바르지 않은 URL: " + url);
        return url.substring(idx + 3);
    }

    /**
     * COPY
     */
    private void copyObject(String source, String destination) {
        try {
            CopyObjectDetails details = CopyObjectDetails.builder()
                    .sourceObjectName(source)
                    .destinationObjectName(destination)
                    .destinationBucket(ociConfig.getBucketName())
                    .destinationNamespace(ociConfig.getNamespace())
                    .destinationRegion(ociConfig.getRegion())
                    .build();

            CopyObjectRequest request = CopyObjectRequest.builder()
                    .bucketName(ociConfig.getBucketName())
                    .namespaceName(ociConfig.getNamespace())
                    .copyObjectDetails(details)
                    .build();

            objectStorage.copyObject(request);

        } catch (BmcException e) {
            // OCI SDK가 던지는 상세 에러 메시지를 로그로 찍습니다.
            log.error(">>> OCI SDK ERROR: Status={}, Code={}, Message={}",
                    e.getStatusCode(), e.getServiceCode(), e.getMessage());
            throw new OciObjectCopyFailException();
        } catch (Exception e) {
            log.error(">>> UNKNOWN COPY ERROR: {}", e.getMessage(), e);
            throw new OciObjectCopyFailException();
        }
    }

    /**
     * DELETE
     */
    private void deleteObject(String objectName) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucketName(ociConfig.getBucketName())
                    .namespaceName(ociConfig.getNamespace())
                    .objectName(objectName)
                    .build();

            objectStorage.deleteObject(request);

        } catch (Exception e) {
            log.error(">>> DELETE ERROR: {}", e.getMessage(), e);
            throw new OciObjectDeleteFailException();
        }
    }
}
