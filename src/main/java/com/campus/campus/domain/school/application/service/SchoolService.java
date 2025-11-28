package com.campus.campus.domain.school.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.school.application.dto.response.SchoolFindResponse;
import com.campus.campus.domain.school.application.mapper.SchoolFindMapper;
import com.campus.campus.domain.school.domain.entity.School;
import com.campus.campus.domain.school.domain.repository.SchoolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolService {
	private final SchoolRepository schoolRepository;
	private final SchoolFindMapper schoolFindMapper;

	public List<SchoolFindResponse> searchSchools(String keyword) {
		List<School> schools = schoolRepository.findBySchoolNameStartingWith(keyword);

		return schoolFindMapper.toSchoolFindResponseList(schools);
	}
}
