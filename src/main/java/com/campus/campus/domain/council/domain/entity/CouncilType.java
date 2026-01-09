package com.campus.campus.domain.council.domain.entity;

import com.campus.campus.domain.user.domain.entity.User;

public enum CouncilType {
	SCHOOL_COUNCIL {
		@Override
		public boolean hasAccess(User user, StudentCouncil writer) {
			return user.getSchool() != null &&
				user.getSchool().getSchoolId().equals(writer.getSchool().getSchoolId());
		}
	},
	COLLEGE_COUNCIL {
		@Override
		public boolean hasAccess(User user, StudentCouncil writer) {
			return user.getCollege() != null &&
				user.getCollege().getCollegeId().equals(writer.getCollege().getCollegeId());
		}
	},
	MAJOR_COUNCIL {
		@Override
		public boolean hasAccess(User user, StudentCouncil writer) {
			return user.getMajor() != null &&
				user.getMajor().getMajorId().equals(writer.getMajor().getMajorId());
		}
	};

	public abstract boolean hasAccess(User user, StudentCouncil writer);
}
