package com.campus.campus.domain.place.domain.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Coordinate(
	double latitude,
	double longitude
) {
}
