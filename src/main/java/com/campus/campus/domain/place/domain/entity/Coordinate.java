package com.campus.campus.domain.place.domain.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Coordinate(
	double latitude,
	double longitude
) {
	public static Coordinate fromNaver(double mapx, double mapy) {
		return new Coordinate(
			mapy / 10_000_000.0,
			mapx / 10_000_000.0
		);
	}
}
