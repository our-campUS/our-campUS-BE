package com.campus.campus.domain.place.domain.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.place.domain.entity.LikedPlace;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.user.domain.entity.User;

public interface LikedPlacesRepository extends JpaRepository<LikedPlace, Long> {

	Optional<LikedPlace> findByUserIdAndPlace_PlaceKey(Long userId, String placeKey);

	@Query("""
			SELECT lp.place.placeId
			FROM LikedPlace lp
			WHERE lp.user.id=:userId
			 and lp.place.placeId in :placeIds
		""")
	Set<Long> findLikedPlaceIds(
		@Param("userId") Long userId,
		@Param("placeIds") Set<Long> placeIds
	);

	boolean existsByUserAndPlace(User user, Place place);
}
