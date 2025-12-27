package com.campus.campus.global.oci.application.service;

import java.net.URI;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.campus.campus.global.config.OciConfig;
import com.campus.campus.global.oci.application.dto.request.PresignedUrlRequestDto;
import com.campus.campus.global.oci.application.dto.response.PresignedUrlResponseDto;
import com.campus.campus.global.oci.exception.OciObjectDeleteFailException;
import com.campus.campus.global.oci.exception.OciPresignedUrlCreateFailException;
import com.campus.campus.global.oci.mapper.PresignedUrlMapper;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresignedUrlService {

	private static final long PRESIGNED_TTL_MS = 10 * 60 * 1000; // 10분
	private final ObjectStorage objectStorage;
	private final OciConfig ociConfig;

	public PresignedUrlResponseDto createPresignedUrl(
		String directory,
		PresignedUrlRequestDto request
	) {
		String objectName =
			directory + "/" + UUID.randomUUID() + request.resolveExtension();

		String uploadUrl = createPresignedPutUrl(objectName);
		String imageUrl = ociConfig.fullObjectUrl(objectName);

		return new PresignedUrlResponseDto(uploadUrl, imageUrl);
	}

	public String createPresignedPutUrl(String objectName) {
		try {
			CreatePreauthenticatedRequestRequest request =
				PresignedUrlMapper.toPutObjectRequest(
					ociConfig.getBucketName(),
					ociConfig.getNamespace(),
					objectName,
					System.currentTimeMillis() + PRESIGNED_TTL_MS
				);

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
		if (imageUrl == null || imageUrl.isBlank())
			return;

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
				PresignedUrlMapper.toDeleteObjectRequest(
					ociConfig.getBucketName(),
					ociConfig.getNamespace(),
					objectName
				);

			objectStorage.deleteObject(request);

			log.info("OCI object deleted: {}", objectName);

		} catch (Exception e) {
			log.error(">>> OCI DELETE ERROR", e);
			throw new OciObjectDeleteFailException();
		}
	}
}
