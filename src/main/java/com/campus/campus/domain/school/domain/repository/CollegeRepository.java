package com.campus.campus.domain.school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.school.domain.entity.College;

public interface CollegeRepository extends JpaRepository<College, Long> {
}
