package com.campus.campus.domain.place.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.place.domain.entity.PlaceImages;

public interface PlaceImagesRepository extends JpaRepository<PlaceImages, Long> {

	List<PlaceImages> findByPlaceKey(String placeKey);
}
