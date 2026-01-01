package com.campus.campus.domain.place.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceImages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long placeImagesId;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "place_id")
	// private Place place;

	@Column(name = "place_key", nullable = false)
	private String placeKey;

	@Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
	private String imageUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "source", nullable = false)
	private ImageSource source;

	public PlaceImages(String placeKey, String imageUrl, ImageSource source) {
		this.placeKey = placeKey;
		this.imageUrl = imageUrl;
		this.source = source;
	}

	//OCI 저장 여부 확인용
	public boolean isOciStored() {
		return this.source == ImageSource.OCI;
	}

	//google 이미지를 OCI 이미지로 (좋아요 시점에서 호출)
	public void updateToOci(String ociImageUrl) {
		this.imageUrl = ociImageUrl;
		this.source = ImageSource.OCI;
	}

}
