package com.campus.campus.domain.council.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;

public interface StudentCouncilRepository extends JpaRepository<StudentCouncil, Long> {

	Optional<StudentCouncil> findByLoginId(String loginId);

	Optional<StudentCouncil> findByEmail(String email);

	@Query("SELECT sc FROM StudentCouncil sc " +
		"LEFT JOIN FETCH sc.school " +
		"LEFT JOIN FETCH sc.college " +
		"LEFT JOIN FETCH sc.major " +
		"WHERE sc.id = :councilId")
	Optional<StudentCouncil> findByIdWithDetails(@Param("councilId") Long councilId);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);
}
