package com.campus.campus.domain.manager.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.manager.domain.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
	Optional<Manager> findByLoginId(String loginId);
}
