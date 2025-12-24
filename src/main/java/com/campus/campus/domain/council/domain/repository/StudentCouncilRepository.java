package com.campus.campus.domain.council.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;

public interface StudentCouncilRepository extends JpaRepository<StudentCouncil, Long> {
	Optional<StudentCouncil> findByLoginIdAndDeletedAtIsNull(String loginId);

	Optional<StudentCouncil> findByEmailAndDeletedAtIsNull(String email);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	boolean existsByEmailAndDeletedAtIsNull(String email);

	Optional<StudentCouncil> findByIdAndDeletedAtIsNull(Long councilId);

	boolean existsByIdAndDeletedAtIsNull(long councilId);
}
