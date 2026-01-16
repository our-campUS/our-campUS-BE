package com.campus.campus.domain.place.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.place.domain.entity.CouncilPartnershipSuggestion;
import com.campus.campus.domain.place.domain.entity.Place;

import jakarta.persistence.LockModeType;

public interface CouncilPartnershipSuggestionRepository extends JpaRepository<CouncilPartnershipSuggestion, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT cps
			FROM CouncilPartnershipSuggestion cps
			WHERE cps.place = :place
			  AND cps.council = :council
		""")
	Optional<CouncilPartnershipSuggestion> findForUpdate(
		@Param("place") Place place,
		@Param("council") StudentCouncil council
	);

}