package com.campus.campus.global.ocr.service;

import com.campus.campus.global.config.OciConfig;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OciObjectDownloadService {

    private final ObjectStorage objectStorage;
    private final OciConfig ociConfig;

    public byte[] download(String objectName) {
        GetObjectRequest request = GetObjectRequest.builder()
                .namespaceName(ociConfig.getNamespace())
                .bucketName(ociConfig.getBucketName())
                .objectName(objectName)
                .build();

        try (InputStream is = objectStorage.getObject(request).getInputStream()) {
            return is.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Object Storage 이미지 다운로드 실패", e);
        }
    }
}


