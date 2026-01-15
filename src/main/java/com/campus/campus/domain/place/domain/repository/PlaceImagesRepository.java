package com.campus.campus.domain.place.domain.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.entity.PlaceImages;

public interface PlaceImagesRepository extends JpaRepository<PlaceImages, Long> {

	List<PlaceImages> findByPlaceKey(String placeKey);

	List<PlaceImages> findAllByPlaceKeyIn(Collection<String> placeKeys);

	@Query("""
			select pi.imageUrl
			from PlaceImages pi
			where pi.place = :place
			order by pi.placeImagesId asc
		""")
	List<String> findImageUrlsByPlace(@Param("place") Place place);
}
