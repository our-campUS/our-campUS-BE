package com.campus.campus.global.oci.mapper;

import java.util.Date;
import java.util.UUID;

import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;

public class PresignedUrlMapper {

	public static CreatePreauthenticatedRequestRequest toPutObjectRequest(
		String bucketName,
		String namespaceName,
		String objectName,
		long expiresAtMillis
	) {
		CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
			.name("upload-" + UUID.randomUUID())
			.objectName(objectName)
			.accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite)
			.timeExpires(new Date(expiresAtMillis))
			.build();

		return CreatePreauthenticatedRequestRequest.builder()
			.bucketName(bucketName)
			.namespaceName(namespaceName)
			.createPreauthenticatedRequestDetails(details)
			.build();
	}

	public static DeleteObjectRequest toDeleteObjectRequest(
		String bucketName,
		String namespaceName,
		String objectName
	) {
		return DeleteObjectRequest.builder()
			.bucketName(bucketName)
			.namespaceName(namespaceName)
			.objectName(objectName)
			.build();
	}
}
