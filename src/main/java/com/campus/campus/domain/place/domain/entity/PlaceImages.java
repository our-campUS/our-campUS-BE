package com.campus.campus.domain.place.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	@Column(name = "place_key", nullable = false)
	private String placeKey;

	@Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
	private String imageUrl;

	public PlaceImages(String placeKey, String imageUrl) {
		this.placeKey = placeKey;
		this.imageUrl = imageUrl;
	}

	//google 이미지를 OCI 이미지로 (좋아요 시점에서 호출)
	public void updateToOci(String ociImageUrl) {
		this.imageUrl = ociImageUrl;
	}

}
