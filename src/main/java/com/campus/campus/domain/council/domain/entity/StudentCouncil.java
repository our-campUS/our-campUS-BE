package com.campus.campus.domain.council.domain.entity;

import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;
import com.campus.campus.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "student_councils")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentCouncil extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_council_id")
	private Long id;

	@Column(name = "login_id")
	private String loginId;

	@Column(name = "password")
	private String password;

	@Column(name = "email")
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "council_type", nullable = false)
	private CouncilType councilType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_id", nullable = false)
	private School school;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "college_id")
	private College college;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "major_id")
	private Major major;

	public void changePassword(String newPassword) {
		this.password = newPassword;
	}

	public void changeEmail(String newEmail) {
		this.email = newEmail;
	}
}
