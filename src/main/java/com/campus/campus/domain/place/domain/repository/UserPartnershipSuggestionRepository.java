package com.campus.campus.domain.place.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.entity.UserPartnershipSuggestion;
import com.campus.campus.domain.user.domain.entity.User;

public interface UserPartnershipSuggestionRepository extends JpaRepository<UserPartnershipSuggestion, Long> {

	boolean existsByUserAndPlace(User user, Place place);
}
