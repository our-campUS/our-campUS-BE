package com.campus.campus.domain.place.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.campus.campus.domain.place.domain.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	// placeKey 기준으로 Place 조회
	Optional<Place> findByPlaceKey(String placeKey);

	List<Place> findByPlaceKeyIn(List<String> placeKeys);

	List<Place> findAllByPlaceKeyIn(List<String> placeKeys);

	@Query("""
    SELECT p.placeId
    FROM Place p
    WHERE p.isPartnership = true
""")
	List<Long> findCurrentPartnershipPlaceIds();

}
