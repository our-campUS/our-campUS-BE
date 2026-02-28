package com.campus.campus.domain.place.domain.entity;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "non_partner_places",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"place_key"})
	}
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NonPartnerPlace extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long nonPartnerPlaceId;

	@Column(name = "place_key", nullable = false, length = 50)
	private String placeKey;

	@Column(name = "place_name", nullable = false, length = 200)
	private String placeName;

	@Column(name = "address", length = 300)
	private String address;

	@Embedded
	private Coordinate coordinate;
}