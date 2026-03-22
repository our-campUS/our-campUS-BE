package com.campus.campus.domain.place.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "places",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = "place_key")
	})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "place_id")
	private Long placeId;

	// 네이버/구글 등 외부 API에서 제공하는 고유 식별자
	@Column(name = "place_key", nullable = false, unique = true)
	private String placeKey;

	//장소명
	@Column(name = "place_name")
	private String placeName;

	@Column(name = "place_category")
	private String placeCategory;

	@Column(name = "phone")
	private String phone;

	@Column(name = "address")
	private String address;

	@Column(name = "naver_place_url")
	private String naverPlaceUrl;

	@Embedded
	private Coordinate coordinate;

	@Column(name = "is_partnership")
	private boolean isPartnership;

	public void makePartnershipTrue() {
		this.isPartnership = true;
	}
}
