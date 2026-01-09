package com.campus.campus.domain.place.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.place.domain.entity.LikedPlace;

public interface LikedPlacesRepository extends JpaRepository<LikedPlace, Long> {

	boolean existsByUserIdAndPlace_PlaceKey(Long userId, String placeKey);

	Optional<LikedPlace> findByUserIdAndPlace_PlaceKey(Long userId, String placeKey);
}
