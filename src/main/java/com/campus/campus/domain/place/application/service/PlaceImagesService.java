package com.campus.campus.domain.place.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	/*
	 * 장소 검색 시 google places로부터 이미지 불러오기
	 */
	public List<String> getPlaceImgs(String placeKey, String name, String address) {
		// DB 확인
		List<String> images = getImages(placeKey);
		if (!images.isEmpty()) {
			return images;
		}

		//최초 검색 시 google에서 이미지 url 가져오기
		List<String> googleImageUrls = googleClient.fetchImages(name, address, 3);
		if (googleImageUrls.isEmpty()) {
			return List.of();
		}
		return googleImageUrls;
	}

	/*
	 * DB 캐시 조회
	 */
	private List<String> getImages(String placeKey) {
		return placeImagesRepository.findByPlaceKey(placeKey).stream()
			.map(PlaceImages::getImageUrl)
			.toList();
	}

	@Transactional
	public void migrateImagestoOci(String placeKey, List<String> imageUrls) {

		//google 이미지 OCI 업로드
		for (String googleUrl : imageUrls) {

			// google 이미지 다운로드
			byte[] bytes = googleClient.downloadImage(googleUrl);

			//OCI 업로드->objectKey 반환
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

			placeImagesRepository.save(
				new PlaceImages(placeKey, googleUrl)
			);
		}
	}
}
