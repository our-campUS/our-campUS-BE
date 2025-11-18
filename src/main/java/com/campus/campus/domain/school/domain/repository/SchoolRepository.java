package com.campus.campus.domain.school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.school.domain.entity.School;

public interface SchoolRepository extends JpaRepository<School, Long> {
}
