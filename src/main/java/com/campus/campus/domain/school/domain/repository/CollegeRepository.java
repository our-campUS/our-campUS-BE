package com.campus.campus.domain.school.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.school.domain.entity.College;

public interface CollegeRepository extends JpaRepository<College, Long> {
	List<College> findBySchool_SchoolIdAndCollegeNameStartingWith(Long schoolId, String keyword);
}
