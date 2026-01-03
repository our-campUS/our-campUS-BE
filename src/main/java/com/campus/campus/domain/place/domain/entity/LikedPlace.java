package com.campus.campus.domain.place.domain.entity;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.global.entity.BaseEntity;

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
@Table(name = "liked_places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class LikedPlace extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long likedPlaceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	public LikedPlace(User user, Place place) {
		// 도메인 규칙 검증
		if (user == null || place == null) {
			throw new IllegalArgumentException("user와 place는 필수입니다.");
		}
		this.user = user;
		this.place = place;
	}

}
