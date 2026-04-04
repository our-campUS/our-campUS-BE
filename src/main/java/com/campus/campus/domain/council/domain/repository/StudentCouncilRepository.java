package com.campus.campus.domain.council.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;

public interface StudentCouncilRepository extends JpaRepository<StudentCouncil, Long> {
	Optional<StudentCouncil> findByLoginIdAndManagerApprovedIsTrueAndDeletedAtIsNull(String loginId);

	Optional<StudentCouncil> findByEmailAndManagerApprovedIsTrueAndDeletedAtIsNull(String email);

	@Query("SELECT sc FROM StudentCouncil sc " +
		"LEFT JOIN FETCH sc.school " +
		"LEFT JOIN FETCH sc.college " +
		"LEFT JOIN FETCH sc.major " +
		"WHERE sc.id = :councilId AND sc.deletedAt IS NULL AND sc.managerApproved IS TRUE")
	Optional<StudentCouncil> findByIdWithDetailsAndManagerApprovedIsTrueAndDeletedAtIsNull(
		@Param("councilId") Long councilId);

	@Query("SELECT sc FROM StudentCouncil sc " +
		"LEFT JOIN FETCH sc.school " +
		"LEFT JOIN FETCH sc.college " +
		"LEFT JOIN FETCH sc.major " +
		"WHERE sc.deletedAt IS NULL AND sc.managerApproved IS FALSE")
	List<StudentCouncil> findByManagerWithDetailsApprovedIsFalseAndDeletedAtIsNull();

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	boolean existsByEmailAndDeletedAtIsNull(String email);

	Optional<StudentCouncil> findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(Long councilId);

	Optional<StudentCouncil> findByIdAndManagerApprovedIsFalseAndDeletedAtIsNull(Long councilId);

	boolean existsByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(Long councilId);

	boolean existsByEmailAndDeletedAtIsNotNull(String email);

	boolean existsByLoginIdAndManagerApprovedIsTrueAndDeletedAtIsNull(String loginId);

	@Query("SELECT s FROM StudentCouncil s " +
		"WHERE s.councilType = :type " +
		"AND s.managerApproved = true " +
		"AND s.deletedAt IS NULL " +
		"AND (" +
		"  (:type = com.campus.campus.domain.council.domain.entity.CouncilType.MAJOR_COUNCIL AND s.major.majorId = :id) OR "
		+
		"  (:type = com.campus.campus.domain.council.domain.entity.CouncilType.COLLEGE_COUNCIL AND s.college.collegeId = :id) OR "
		+
		"  (:type = com.campus.campus.domain.council.domain.entity.CouncilType.SCHOOL_COUNCIL AND s.school.schoolId = :id)"
		+
		")")
	Optional<StudentCouncil> findActiveCouncilByTypeAndId(@Param("id") Long id, @Param("type") CouncilType type);

	Optional<StudentCouncil> findByIdAndDeletedAtIsNull(Long councilId);

	List<StudentCouncil> findAllByPendingEmailIsNotNullAndDeletedAtIsNull();

	Optional<StudentCouncil> findByIdAndPendingEmailIsNotNullAndDeletedAtIsNull(Long councilId);

}
