package com.campus.campus.domain.place.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.place.domain.entity.NonPartnerPlace;

public interface NonPartnerPlaceRepository extends JpaRepository<NonPartnerPlace, Long> {

	Optional<NonPartnerPlace> findByPlaceKey(String placeKey);

}
