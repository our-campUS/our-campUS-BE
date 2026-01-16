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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
	name = "reviews",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_review_receipt",
			columnNames = {"business_number", "confirm_number"}
		)
	}
)
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String content;

	private Double star;

	//영수증 제휴 인증 여부
	@Column(name = "is_verified")
	private boolean isVerified;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	//승인 번호
	@Column(name = "confirm_number", nullable = false)
	private String confirmNumber;

	//사업자 번호
	@Column(name = "business_number", nullable = false)
	private String businessNumber;

	public void update(
		String content,
		double star
	) {
		this.content = content;
		this.star = star;
	}

	public void verify() {
		this.isVerified = true;
	}
}
