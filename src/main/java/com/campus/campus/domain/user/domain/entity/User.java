package com.campus.campus.domain.user.domain.entity;

import java.time.LocalDateTime;

import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;
import com.campus.campus.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {
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

	@Column(name = "campus_nickname")
	private String campusNickname;

	@Column(name = "reward_needed")
	@Builder.Default
	private boolean rewardNeeded = false;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_id")
	private School school;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "college_id")
	private College college;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "major_id")
	private Major major;

	public void updateProfile(School school, College college, Major major) {
		this.school = school;
		this.college = college;
		this.major = major;
	}

	public void updateCampusNickname(String campusNickname) {
		this.campusNickname = campusNickname;
	}

	public void delete(LocalDateTime now) {
		this.deletedAt = now;
	}

	public boolean isProfileNotCompleted() {
		return this.school == null || this.major == null;
	}

	public void updateRewardNeeded(boolean rewardNeeded) {
		this.rewardNeeded = rewardNeeded;
	}
}
