package com.campus.campus.domain.school.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.school.domain.entity.Major;

public interface MajorRepository extends JpaRepository<Major, Long> {
	List<Major> findBySchool_SchoolId(Long schoolId);
}
