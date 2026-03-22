package com.campus.campus.domain.place.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.place.domain.entity.LikedPlace;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.user.domain.entity.User;

public interface LikedPlacesRepository extends JpaRepository<LikedPlace, Long> {

	@Query("""
		SELECT lp
		FROM LikedPlace lp
		JOIN FETCH lp.place p
		WHERE lp.user.id = :userId
		ORDER BY lp.createdAt DESC
	""")
	List<LikedPlace> findAllWithPlaceByUserId(@Param("userId") Long userId);

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

	@Query("""
	SELECT lp.place.placeKey
	FROM LikedPlace lp
	WHERE lp.user.id = :userId
	  AND lp.place.placeKey IN :placeKeys
""")
	Set<String> findLikedPlaceKeys(@Param("userId") Long userId, @Param("placeKeys") List<String> placeKeys);

	@Query("""
	SELECT lp
	FROM LikedPlace lp
	JOIN FETCH lp.place p
	WHERE lp.user.id = :userId
	  AND (:cursor IS NULL OR lp.likedPlaceId < :cursor)
	ORDER BY lp.likedPlaceId DESC
""")
	List<LikedPlace> findLikedPlacesWithCursor(
		@Param("userId") Long userId,
		@Param("cursor") Long cursor,
		Pageable pageable
	);
}
