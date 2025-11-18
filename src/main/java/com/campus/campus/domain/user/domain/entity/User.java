package com.campus.campus.domain.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "kakao_id")
	private Long kakaoId;

	@Column(name = "name")
	private String nickname;

	@Column(name = "email")
	private String email;

	@Column(name = "profile_image")
	private String profileImage;

	@Enumerated(EnumType.STRING)
	@Column(name = "school")
	private School school;

	@Column(name = "college")
	private String college;

	@Column(name = "major")
	private String major;

	public void updateProfile(School school, String college, String major) {
		this.school = school;
		this.college = college;
		this.major = major;
	}

	public boolean isProfileNotCompleted() {
		return this.school == null;
	}
}
