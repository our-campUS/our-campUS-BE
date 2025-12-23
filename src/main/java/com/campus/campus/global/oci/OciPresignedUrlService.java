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
import java.net.URI;
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

    private static final long PRESIGNED_TTL_MS = 10 * 60 * 1000; // 10분

    public String createPresignedPutUrl(String objectName) {
        try {
            CreatePreauthenticatedRequestDetails details =
                    CreatePreauthenticatedRequestDetails.builder()
                            .name("upload-" + UUID.randomUUID())
                            .objectName(objectName)
                            .accessType(
                                    CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite
                            )
                            .timeExpires(
                                    new Date(System.currentTimeMillis() + PRESIGNED_TTL_MS)
                            )
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
            log.error(">>> PRESIGNED URL CREATE ERROR", e);
            throw new OciPresignedUrlCreateFailException();
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        String objectName = extractObjectNameFromUrl(imageUrl);
        deleteObject(objectName);
    }

    private String extractObjectNameFromUrl(String url) {
        try {
            URI uri = URI.create(url);
            String path = uri.getPath();
            int idx = path.indexOf("/o/");
            if (idx == -1) {
                throw new IllegalArgumentException("Invalid OCI object URL");
            }
            return path.substring(idx + 3);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid OCI object URL: " + url);
        }
    }

    /**
     * OCI Object 삭제
     */
    private void deleteObject(String objectName) {
        try {
            DeleteObjectRequest request =
                    DeleteObjectRequest.builder()
                            .bucketName(ociConfig.getBucketName())
                            .namespaceName(ociConfig.getNamespace())
                            .objectName(objectName)
                            .build();

            objectStorage.deleteObject(request);

            log.info("OCI object deleted: {}", objectName);

        } catch (Exception e) {
            log.error(">>> OCI DELETE ERROR", e);
            throw new OciObjectDeleteFailException();
        }
    }
}
