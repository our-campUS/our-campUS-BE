package com.campus.campus.domain.place.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipPlaceSummary;
import com.campus.campus.domain.place.domain.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	// placeKey 기준으로 Place 조회
	Optional<Place> findByPlaceKey(String placeKey);

	//제휴 가게 조회(학과)
	@Query("""
			select new com.campus.campus.domain.place.application.dto.response.partnership.PartnershipPlaceSummary(
				p.placeId,
				p.placeKey,
				p.placeName,
				p.placeCategory,
				p.address,
				p.coordinate.latitude,
				p.coordinate.longitude,
		
				council.councilType
			)
			from StudentCouncilPost post
			join post.place p
			join post.writer council
			where post.category = :category
			  and (
		   	(council.councilType = 'MAJOR'
			 and council.major.majorId = :majorId)
		  or (council.councilType = 'COLLEGE'
			 and council.college.collegeId = :collegeId)
		  or (council.councilType = 'SCHOOL'
			 and council.school.schoolId = :schoolId)
		 	)
			and (:cursor is null or p.placeId<:cursor)
			order by p.placeId desc
		""")
	List<PartnershipPlaceSummary> findPartnershipPlaces(
		@Param("category") PostCategory category,
		@Param("majorId") Long majorId,
		@Param("collegeId") Long collegeId,
		@Param("schoolId") Long schoolId,
		@Param("cursor") Long cursor,
		Pageable pageable
	);
}
