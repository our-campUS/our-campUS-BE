package com.campus.campus.domain.school.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.school.application.dto.response.CollegeFindResponse;
import com.campus.campus.domain.school.application.dto.response.MajorFindResponse;
import com.campus.campus.domain.school.application.dto.response.SchoolFindResponse;
import com.campus.campus.domain.school.application.exception.SchoolNotFoundException;
import com.campus.campus.domain.school.application.mapper.SchoolFindMapper;
import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;
import com.campus.campus.domain.school.domain.repository.CollegeRepository;
import com.campus.campus.domain.school.domain.repository.MajorRepository;
import com.campus.campus.domain.school.domain.repository.SchoolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolService {
	private final SchoolRepository schoolRepository;
	private final CollegeRepository collegeRepository;
	private final MajorRepository majorRepository;
	private final SchoolFindMapper schoolFindMapper;

	public List<SchoolFindResponse> searchSchools(String keyword) {
		List<School> schools = schoolRepository.findBySchoolNameStartingWith(keyword);

		return schoolFindMapper.toSchoolFindResponseList(schools);
	}

	public List<CollegeFindResponse> searchColleges(Long schoolId, String keyword) {
		schoolRepository.findById(schoolId)
			.orElseThrow(SchoolNotFoundException::new);

		List<College> colleges = collegeRepository.findBySchool_SchoolIdAndCollegeNameStartingWith(schoolId, keyword);

		return schoolFindMapper.toCollegeFindResponseList(colleges);
	}

	public List<MajorFindResponse> searchMajors(Long schoolId, String keyword) {
		schoolRepository.findById(schoolId)
			.orElseThrow(SchoolNotFoundException::new);

		List<Major> majors = majorRepository.findBySchool_SchoolIdAndMajorNameStartingWith(schoolId, keyword);

		return schoolFindMapper.toMajorFindResponseList(majors);
	}
}
