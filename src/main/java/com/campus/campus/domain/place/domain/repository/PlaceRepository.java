package com.campus.campus.domain.place.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.place.domain.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	Place save(Place place);
}
