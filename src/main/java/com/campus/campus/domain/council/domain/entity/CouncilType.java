package com.campus.campus.domain.council.domain.entity;

import com.campus.campus.domain.user.domain.entity.User;

public enum CouncilType {
	SCHOOL_COUNCIL {
		@Override
		public boolean hasAccess(User user, StudentCouncil writer) {
			return user.getSchool() != null &&
				user.getSchool().getSchoolId().equals(writer.getSchool().getSchoolId());
		}

		@Override public String topic(StudentCouncil writer) {
			return "school_" + writer.getSchool().getSchoolId();
		}

		@Override public Long scopeId(StudentCouncil writer) {
			return writer.getSchool().getSchoolId();
		}

		@Override public Scope scope() { return Scope.SCHOOL; }
	},
	COLLEGE_COUNCIL {
		@Override
		public boolean hasAccess(User user, StudentCouncil writer) {
			return user.getCollege() != null &&
				user.getCollege().getCollegeId().equals(writer.getCollege().getCollegeId());
		}

		@Override public String topic(StudentCouncil writer) {
			return "college_" + writer.getCollege().getCollegeId();
		}

		@Override public Long scopeId(StudentCouncil writer) {
			return writer.getCollege().getCollegeId();
		}

		@Override public Scope scope() { return Scope.COLLEGE; }
	},
	MAJOR_COUNCIL {
		@Override
		public boolean hasAccess(User user, StudentCouncil writer) {
			return user.getMajor() != null &&
				user.getMajor().getMajorId().equals(writer.getMajor().getMajorId());
		}

		@Override public String topic(StudentCouncil writer) {
			return "major_" + writer.getMajor().getMajorId();
		}

		@Override public Long scopeId(StudentCouncil writer) {
			return writer.getMajor().getMajorId();
		}

		@Override public Scope scope() { return Scope.MAJOR; }
	};

	public enum Scope { SCHOOL, COLLEGE, MAJOR }

	public abstract boolean hasAccess(User user, StudentCouncil writer);
	public abstract String topic(StudentCouncil writer);

}
