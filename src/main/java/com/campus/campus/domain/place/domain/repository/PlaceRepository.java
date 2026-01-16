package com.campus.campus.domain.place.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.place.domain.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	// placeKey 기준으로 Place 조회
	Optional<Place> findByPlaceKey(String placeKey);

}
