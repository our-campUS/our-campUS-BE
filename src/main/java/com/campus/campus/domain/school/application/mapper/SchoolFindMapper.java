package com.campus.campus.domain.school.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.school.application.dto.response.CollegeFindResponse;
import com.campus.campus.domain.school.application.dto.response.MajorFindResponse;
import com.campus.campus.domain.school.application.dto.response.SchoolFindResponse;
import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;

@Component
public class SchoolFindMapper {
	public List<SchoolFindResponse> toSchoolFindResponseList(List<School> schools) {
		return schools.stream()
			.map(this::toSchoolFindResponse)
			.toList();
	}

	public SchoolFindResponse toSchoolFindResponse(School school) {
		return new SchoolFindResponse(
			school.getSchoolId(),
			school.getSchoolName()
		);
	}

	public List<CollegeFindResponse> toCollegeFindResponseList(List<College> colleges) {
		return colleges.stream()
			.map(this::toCollegeFindResponse)
			.toList();
	}

	public CollegeFindResponse toCollegeFindResponse(College college) {
		return new CollegeFindResponse(
			college.getCollegeId(),
			college.getCollegeName()
		);
	}

	public List<MajorFindResponse> toMajorFindResponseList(List<Major> majors) {
		return majors.stream()
			.map(this::toMajorFindResponse)
			.toList();
	}

	public MajorFindResponse toMajorFindResponse(Major major) {
		return new MajorFindResponse(
			major.getCollege().getCollegeId(),
			major.getCollege().getCollegeName(),
			major.getMajorId(),
			major.getMajorName()
		);
	}
}
