package com.campus.campus.domain.place.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.place.domain.entity.ImageSource;
import com.campus.campus.domain.place.domain.entity.PlaceImages;
import com.campus.campus.domain.place.domain.repository.PlaceImagesRepository;
import com.campus.campus.domain.place.infrastructure.google.GooglePlaceClientImpl;
import com.campus.campus.global.oci.application.dto.request.PresignedUrlRequestDto;
import com.campus.campus.global.oci.application.dto.response.PresignedUrlResponseDto;
import com.campus.campus.global.oci.application.service.PresignedUrlService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceImagesService {

	private final GooglePlaceClientImpl googleClient;
	private final PlaceImagesRepository placeImagesRepository;
	private final PresignedUrlService presignedUrlService;

	public List<String> getPlaceImgs(String placeKey, String name, String address) {

		// DB 확인
		List<PlaceImages> savedImages = placeImagesRepository.findByPlaceKey(placeKey);
		if (!savedImages.isEmpty()) {
			return savedImages.stream()
				.map(PlaceImages::getImageUrl)
				.toList();
		}

		//최초 검색 시 google에서 이미지 url 가져오기
		List<String> googleImageUrls = googleClient.fetchImages(name, address, 3);

		if (googleImageUrls.isEmpty()) {
			return List.of();
		}

		// google 이미지 url을 그대로 DB에 저장
		List<String> storedImageUrls = new ArrayList<>();

		for (String googleImageUrl : googleImageUrls) {
			placeImagesRepository.save(
				new PlaceImages(placeKey, googleImageUrl, ImageSource.GOOGLE)
			);
			storedImageUrls.add(googleImageUrl);
		}
		return storedImageUrls;
	}

	@Transactional
	public void migrateImagestoOci(String placeKey) {
		List<PlaceImages> images = placeImagesRepository.findByPlaceKey(placeKey);

		//google 이미지 OCI 업로드
		for (PlaceImages image : images) {

			// 이미 OCI로 옮긴 건 스킵
			if (image.isOciStored()) {
				continue;
			}

			// Google → OCI 업로드
			byte[] bytes = googleClient.downloadImage(image.getImageUrl());

			PresignedUrlResponseDto presigned =
				presignedUrlService.createPresignedUrl(
					"places",
					new PresignedUrlRequestDto("image/jpeg")
				);

			presignedUrlService.uploadToOci(
				presigned.uploadUrl(),
				bytes,
				"image/jpeg"
			);

			// URL 교체
			image.updateToOci(presigned.imageUrl());
		}
	}
}
