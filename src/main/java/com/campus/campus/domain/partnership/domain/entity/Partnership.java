package com.campus.campus.domain.partnership.domain.entity;

import java.time.LocalDateTime;

import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.place.domain.entity.Place;

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
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Partnership {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "partnership_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private StudentCouncilPost post;

	@Enumerated(EnumType.STRING)
	private PartnershipStatus status; //ACTIVE, EXPIRED, SUSPENDED

	private LocalDateTime startDate;
	private LocalDateTime endDate;

}
