package com.campus.campus.domain.review.domain.entity;

import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String content;

	private Double star;

	//영수증 제휴 인증 여부
	@Column(name = "is_verified")
	private boolean isVerified;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReviewStatus status = ReviewStatus.VISIBLE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

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

	//금칙어 검토 대상으로 표시 (비공개)
	public void markAsNeedsReview() { this.status = ReviewStatus.PENDING_REVIEW; }

	//정상 공개 상태로 표시
	public void markAsVisible() { this.status = ReviewStatus.VISIBLE; }

	public boolean isVisible() { return this.status == ReviewStatus.VISIBLE; }

}
