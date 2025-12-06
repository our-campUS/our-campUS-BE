package com.campus.campus.domain.mail.application.service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.mail.application.dto.request.EmailVerificationConfirmRequest;
import com.campus.campus.domain.mail.application.exception.EmailVerificationNotFoundException;
import com.campus.campus.domain.mail.application.exception.VerificationCodeExpiredException;
import com.campus.campus.domain.mail.application.exception.VerificationCodeNotMatchException;
import com.campus.campus.domain.mail.application.mapper.EmailVerificationMapper;
import com.campus.campus.domain.mail.domain.entity.EmailVerification;
import com.campus.campus.domain.mail.domain.entity.VerificationType;
import com.campus.campus.domain.mail.domain.repository.EmailVerificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailVerificationService {
	private static final long EXPIRE_TIME = 5L;

	private final JavaMailSender javaMailSender;
	private final EmailVerificationMapper emailVerificationMapper;
	private final EmailVerificationRepository emailVerificationRepository;

	@Transactional
	public void sendSignUpVerificationCode(String email) {
		String code = createCode();
		LocalDateTime expireTime = LocalDateTime.now().plusMinutes(EXPIRE_TIME);

		EmailVerification emailVerification = emailVerificationMapper
			.createSignupEmailVerification(email, code, expireTime);
		emailVerificationRepository.save(emailVerification);

		sendSignUpVerificationMail(email, code);
	}

	public void sendSignUpVerificationMail(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("[Campus] 학생회 회원가입 이메일 인증 코드");
		message.setText(
			"""
				Campus 학생회 회원가입을 위한 이메일 인증 코드입니다.
				
				인증 코드 : %s
				
				5분 이내에 입력해주세요.
				""".formatted(code)
		);

		javaMailSender.send(message);
	}

	@Transactional
	public void sendFindIdVerificationCode(String email) {
		String code = createCode();
		LocalDateTime expireTime = LocalDateTime.now().plusMinutes(EXPIRE_TIME);

		EmailVerification emailVerification = emailVerificationMapper
			.createFindIdEmailVerification(email, code, expireTime);
		emailVerificationRepository.save(emailVerification);

		sendFindIdVerificationMail(email, code);
	}

	public void sendFindIdVerificationMail(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("[Campus] 학생대표자 아이디 찾기 이메일 인증 코드");
		message.setText(
			"""
				Campus 학생대표자 아이디 찾기를 위한 이메일 인증 코드입니다.
				
				인증 코드 : %s
				
				5분 이내에 입력해주세요.
				""".formatted(code)
		);

		javaMailSender.send(message);
	}

	@Transactional
	public void sendFindPasswordVerificationCode(String email) {
		String code = createCode();
		LocalDateTime expireTime = LocalDateTime.now().plusMinutes(EXPIRE_TIME);

		EmailVerification emailVerification = emailVerificationMapper
			.createFindPasswordEmailVerification(email, code, expireTime);
		emailVerificationRepository.save(emailVerification);

		sendFindPasswordVerificationMail(email, code);
	}

	public void sendFindPasswordVerificationMail(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("[Campus] 학생대표자 비밀번호 찾기 이메일 인증 코드");
		message.setText(
			"""
				Campus 학생대표자 비밀번호 찾기를 위한 이메일 인증 코드입니다.
				
				인증 코드 : %s
				
				5분 이내에 입력해주세요.
				""".formatted(code)
		);

		javaMailSender.send(message);
	}

	@Transactional
	public void verifyCode(EmailVerificationConfirmRequest emailVerificationConfirmRequest,
		VerificationType verificationType) {
		EmailVerification emailVerification = emailVerificationRepository.
			findTopByEmailAndVerificationTypeOrderByEmailVerificationIdDesc(emailVerificationConfirmRequest.email(),
				verificationType)
			.orElseThrow(EmailVerificationNotFoundException::new);

		if (emailVerification.isExpired()) {
			emailVerificationRepository.delete(emailVerification);
			throw new VerificationCodeExpiredException();
		}

		if (!emailVerification.getCode().equals(emailVerificationConfirmRequest.code())) {
			throw new VerificationCodeNotMatchException();
		}

		emailVerification.verify();
	}

	private String createCode() {
		int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
		return String.valueOf(code);
	}
}
