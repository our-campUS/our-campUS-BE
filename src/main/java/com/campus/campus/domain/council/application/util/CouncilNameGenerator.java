package com.campus.campus.domain.council.application.util;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;

@Component
public class CouncilNameGenerator {
	public String buildCouncilName(StudentCouncil studentCouncil) {
		String schoolName = studentCouncil.getSchool() != null ? studentCouncil.getSchool().getSchoolName() : "";
		CouncilType councilType = studentCouncil.getCouncilType();

		return switch (councilType) {
			case SCHOOL_COUNCIL -> String.format("%s 총학생회", schoolName).trim();
			case COLLEGE_COUNCIL -> String.format("%s %s 학생회", schoolName, getCollegeName(studentCouncil)).trim();
			case MAJOR_COUNCIL -> String.format("%s %s 학생회", schoolName, getMajorName(studentCouncil)).trim();
		};
	}

	private String getCollegeName(StudentCouncil studentCouncil) {
		if (studentCouncil.getCollege() == null) {
			return "";
		}
		return studentCouncil.getCollege().getCollegeName();
	}

	private String getMajorName(StudentCouncil studentCouncil) {
		if (studentCouncil.getMajor() == null) {
			return "";
		}
		return studentCouncil.getMajor().getMajorName();
	}
}
