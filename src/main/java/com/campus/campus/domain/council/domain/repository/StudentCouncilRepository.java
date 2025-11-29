package com.campus.campus.domain.council.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;

public interface StudentCouncilRepository extends JpaRepository<StudentCouncil, Long> {
	Optional<StudentCouncil> findByLoginId(String loginId);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);
}
