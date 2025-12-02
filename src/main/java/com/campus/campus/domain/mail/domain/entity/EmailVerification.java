package com.campus.campus.domain.mail.domain.entity;

import java.time.LocalDateTime;

import com.campus.campus.global.entity.BaseEntity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "email_verifications")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailVerification extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "email_verification_id")
	private Long emailVerificationId;

	@Column(name = "email", nullable = false)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "verification_type", nullable = false)
	private VerificationType verificationType;

	@Column(name = "code", nullable = false, length = 6)
	private String code;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "verified", nullable = false)
	private boolean verified;

	public boolean isExpired() {
		return expiresAt.isBefore(LocalDateTime.now());
	}

	public void verify() {
		this.verified = true;
	}
}
