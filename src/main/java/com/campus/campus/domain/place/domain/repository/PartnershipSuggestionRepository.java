package com.campus.campus.domain.place.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.place.domain.entity.CouncilPartnershipSuggestion;
import com.campus.campus.domain.place.domain.entity.Place;

public interface PartnershipSuggestionRepository extends JpaRepository<CouncilPartnershipSuggestion, Long> {

	Optional<CouncilPartnershipSuggestion> findByPlaceAndCouncil(Place place, StudentCouncil council);
}
