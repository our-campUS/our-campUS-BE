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
	private static final String WITHDRAWN_USER_NICKNAME = "---";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "kakao_id", unique = true)
	private Long kakaoId;

	@Column(name = "apple_id", unique = true)
	private String appleId;

	@Getter(AccessLevel.NONE)
	@Column(name = "apple_refresh_token", length = 4096)
	private String encryptedAppleRefreshToken;

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

	@Column(name = "last_profile_updated_at")
	private LocalDateTime lastProfileUpdatedAt;

	public void updateProfile(School school, College college, Major major) {
		this.school = school;
		this.college = college;
		this.major = major;
		this.lastProfileUpdatedAt = LocalDateTime.now();
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

	public void updateProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public void updateEncryptedAppleRefreshToken(String encryptedAppleRefreshToken) {
		this.encryptedAppleRefreshToken = encryptedAppleRefreshToken;
	}

	public String encryptedAppleRefreshTokenForRevocation() {
		return encryptedAppleRefreshToken;
	}

	public void scrubPersonalInfo() {
		this.kakaoId = null;
		this.appleId = null;
		this.encryptedAppleRefreshToken = null;
		this.email = null;
		this.nickname = WITHDRAWN_USER_NICKNAME;
		this.campusNickname = WITHDRAWN_USER_NICKNAME;
		this.profileImage = null;
	}
}
