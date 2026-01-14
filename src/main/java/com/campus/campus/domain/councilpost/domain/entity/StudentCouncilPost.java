package com.campus.campus.domain.councilpost.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.place.domain.entity.Place;
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
public class StudentCouncilPost extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id")
	private StudentCouncil writer;

	@Enumerated(EnumType.STRING)
	private PostCategory category;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	@Column(name = "detailed_location")
	private String detailedLocation;

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	private String thumbnailImageUrl;

	@Enumerated(EnumType.STRING)
	private ThumbnailIcon thumbnailIcon;

	public void update(
		String title,
		String content,
		Place place,
		String detailedLocation,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime,
		String thumbnailImageUrl,
		ThumbnailIcon thumbnailIcon,
		PostCategory category) {

		this.title = title;
		this.content = content;
		this.place = place;
		this.detailedLocation = detailedLocation;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.category = category;

		if (thumbnailImageUrl != null)
			this.thumbnailImageUrl = thumbnailImageUrl;

		if (thumbnailIcon != null)
			this.thumbnailIcon = thumbnailIcon;
	}

	public boolean isEvent() {
		return this.category == PostCategory.EVENT;
	}

	public LocalDate getDisplayStartDate() {
		return startDateTime != null ? startDateTime.toLocalDate() : null;
	}

	public LocalDate getDisplayEndDate() {
		return endDateTime != null ? endDateTime.toLocalDate() : null;
	}

	public boolean isWrittenByCouncil(Long councilId) {
		return writer != null && writer.getId().equals(councilId);
	}

	public boolean isClosed(LocalDateTime now) {
		if (this.category == PostCategory.EVENT) {
			return this.startDateTime != null && this.startDateTime.isBefore(now);
		} else {
			return this.endDateTime != null && this.endDateTime.isBefore(now);
		}
	}
}
