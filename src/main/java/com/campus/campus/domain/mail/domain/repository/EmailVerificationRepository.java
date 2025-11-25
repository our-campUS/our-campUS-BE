package com.campus.campus.domain.mail.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.mail.domain.entity.EmailVerification;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
	Optional<EmailVerification> findTopByEmailOrderByEmailVerificationIdDesc(String email);

	boolean existsByEmailAndVerifiedIsTrue(String email);

}
