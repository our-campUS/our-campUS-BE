package com.campus.campus.domain.place.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.place.domain.entity.LikedPlace;

public interface LikedPlacesRepository extends JpaRepository<LikedPlace, Long> {

	boolean existsByUserIdAndPlaceKey(Long userId, String placeKey);

	Optional<LikedPlace> findByUserIdAndPlaceKey(Long userId, String placeKey);
}
