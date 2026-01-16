package com.campus.campus.domain.council.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

	Optional<StudentCouncil> findByMajor_MajorIdAndDeletedAtIsNull(Long majorId);

	Optional<StudentCouncil> findByCollege_CollegeIdAndDeletedAtIsNull(Long collegeId);

	Optional<StudentCouncil> findBySchool_SchoolIdAndDeletedAtIsNull(Long schoolId);
}
