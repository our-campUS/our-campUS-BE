package com.campus.campus.domain.place.domain.entity;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "council_partnership_suggestion")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouncilPartnershipSuggestion extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_council_id", nullable = false)
	private StudentCouncil council;

	private int requestCount;

	public void increase() {
		this.requestCount++;
	}

	public static CouncilPartnershipSuggestion create(
		Place place,
		StudentCouncil council
	) {
		CouncilPartnershipSuggestion demand = new CouncilPartnershipSuggestion();
		demand.place = place;
		demand.council = council;
		demand.requestCount = 0;
		return demand;
	}
}



