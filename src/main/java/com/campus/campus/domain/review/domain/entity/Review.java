package com.campus.campus.domain.review.domain.entity;

import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.global.entity.BaseEntity;

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
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long reviewId;

	private int grade; //별점

	@Column(name = "positive_points")
	private String positivePoints; //좋았던 점

	@Column(name = "negative_points")
	private String negativePoints; //나빴던 점

	private String keyword;

	@Column(name = "is_verified")
	private Boolean isVerified; //영수증 OCR 인증 여부

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id", nullable = false)
	private User writer;

}
