package com.campus.campus.domain.partnership.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.partnership.domain.entity.Partnership;
import com.campus.campus.domain.partnership.domain.entity.PartnershipStatus;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipPlaceSummary;

public interface PartnershipRepository extends JpaRepository<Partnership, Long> {

	@Query("""
			select new com.campus.campus.domain.place.application.dto.response.partnership.PartnershipPlaceSummary(
				pt.id,
				p.placeId,
				p.placeKey,
				p.placeName,
				p.placeCategory,
				p.address,
				p.coordinate.latitude,
				p.coordinate.longitude,
				council.councilType
			)
			from Partnership pt
			join pt.place p
			join pt.post post
			join post.writer council
			where pt.status = :activeStatus
			  and :today between pt.startDate and pt.endDate
			  and council.deletedAt is null
			  and (
				   (council.councilType = :majorType
				    and council.major.majorId = :majorId)
				or (council.councilType = :collegeType
				    and council.college.collegeId = :collegeId)
				or (council.councilType = :schoolType
				    and council.school.schoolId = :schoolId)
			  )
			  and (:cursor is null or pt.id < :cursor)
			order by pt.id desc
		""")
	List<PartnershipPlaceSummary> findActivePartnershipPlaces(
		@Param("today") LocalDateTime today,
		@Param("majorId") Long majorId,
		@Param("collegeId") Long collegeId,
		@Param("schoolId") Long schoolId,
		@Param("cursor") Long cursor,
		@Param("activeStatus") PartnershipStatus activeStatus,
		@Param("majorType") CouncilType majorType,
		@Param("collegeType") CouncilType collegeType,
		@Param("schoolType") CouncilType schoolType,
		Pageable pageable
	);
}

