package com.campus.campus.domain.manager.application.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.inquiry.application.dto.response.InquiryListItemResponse;
import com.campus.campus.domain.inquiry.application.exception.InquiryNotFoundException;
import com.campus.campus.domain.inquiry.application.mapper.InquiryMapper;
import com.campus.campus.domain.inquiry.domain.entity.Inquiry;
import com.campus.campus.domain.inquiry.domain.entity.WriterType;
import com.campus.campus.domain.inquiry.domain.repository.InquiryRepository;
import com.campus.campus.domain.manager.application.dto.request.CouncilApproveOrDenyRequest;
import com.campus.campus.domain.manager.application.dto.request.InquiryAnswerRequest;
import com.campus.campus.domain.manager.application.dto.request.InquirySearchCondition;
import com.campus.campus.domain.manager.application.dto.request.ManagerLoginRequest;
import com.campus.campus.domain.manager.application.dto.request.RewardRequest;
import com.campus.campus.domain.manager.application.dto.response.CertifyRequestCouncilListResponse;
import com.campus.campus.domain.manager.application.dto.response.CertifyRequestCouncilResponse;
import com.campus.campus.domain.manager.application.dto.response.CouncilApproveOrDenyResponse;
import com.campus.campus.domain.manager.application.dto.response.ManagerLoginResponse;
import com.campus.campus.domain.manager.application.dto.response.StampRewardNeededUserListResponse;
import com.campus.campus.domain.manager.application.exception.ManagerNotFoundException;
import com.campus.campus.domain.manager.application.exception.PasswordNotCorrectException;
import com.campus.campus.domain.manager.application.mapper.ManagerMapper;
import com.campus.campus.domain.manager.domain.entity.Manager;
import com.campus.campus.domain.manager.domain.repository.ManagerRepository;
import com.campus.campus.domain.notification.application.service.NotificationService;
import com.campus.campus.domain.notification.application.service.StudentCouncilNotificationService;
import com.campus.campus.domain.stamp.domain.entity.Reward;
import com.campus.campus.domain.stamp.domain.repository.RewardRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.util.jwt.JwtProvider;
import com.campus.campus.global.util.jwt.application.service.RedisTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {
	private final StudentCouncilRepository studentCouncilRepository;
	private final ManagerRepository managerRepository;
	private final UserRepository userRepository;
	private final RewardRepository rewardRepository;
	private final InquiryRepository inquiryRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;
	private final RedisTokenService redisTokenService;
	private final ManagerMapper managerMapper;
	private final InquiryMapper inquiryMapper;
	private final JavaMailSender javaMailSender;
	private final ApplicationEventPublisher eventPublisher;
	private final NotificationService notificationService;
	private final StudentCouncilNotificationService councilNotificationService;

	@Value("${jwt.refresh.expiration-seconds}")
	private long refreshTokenExpirationSeconds;

	public ManagerLoginResponse login(ManagerLoginRequest managerLoginRequest) {
		Manager manager = managerRepository.findByLoginId(managerLoginRequest.loginId())
			.orElseThrow(ManagerNotFoundException::new);

		if (!passwordEncoder.matches(managerLoginRequest.password(), manager.getPassword())) {
			throw new PasswordNotCorrectException();
		}

		String accessToken = jwtProvider.createManagerAccessToken(manager.getId());
		String refreshToken = jwtProvider.createManagerRefreshToken(manager.getId());

		redisTokenService.setRefreshToken("MANAGER", String.valueOf(manager.getId()), refreshToken,
			refreshTokenExpirationSeconds);

		return managerMapper.toManagerLoginResponse(manager, accessToken, refreshToken);
	}

	@Transactional
	public CouncilApproveOrDenyResponse approveOrDenyCouncil(Long councilId,
		CouncilApproveOrDenyRequest councilApproveOrDenyRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository
			.findByIdAndManagerApprovedIsFalseAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		boolean certifyResult = councilApproveOrDenyRequest.certifyResult();

		if (certifyResult) {
			studentCouncil.managerApprove();
			studentCouncil.updateCouncilPresident(councilApproveOrDenyRequest.councilPresident());
			studentCouncilRepository.save(studentCouncil);

			sendCouncilApprovedMail(studentCouncil.getEmail());
		} else {
			sendCouncilDeniedMail(studentCouncil.getEmail());
		}

		return managerMapper.toCouncilApproveOrDenyResponse(studentCouncil.getId(), certifyResult,
			studentCouncil.getCouncilPresident());
	}

	public List<CertifyRequestCouncilListResponse> getCertifyRequestCouncils() {
		return studentCouncilRepository.findByManagerWithDetailsApprovedIsFalseAndDeletedAtIsNull()
			.stream()
			.map(managerMapper::toCertifyRequestCouncilListResponse)
			.toList();
	}

	public CertifyRequestCouncilResponse getCertifyRequestCouncil(Long councilId) {
		StudentCouncil studentCouncil = studentCouncilRepository
			.findByIdAndManagerApprovedIsFalseAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		return managerMapper.toCertifyRequestCouncilResponse(studentCouncil);
	}

	public List<StampRewardNeededUserListResponse> getStampRewardNeededUserList() {
		List<Object[]> rewardNeededUsers = userRepository.findRewardNeededUsersWithStampCount();

		return rewardNeededUsers.stream()
			.map(rewardNeededUser -> {
					User user = (User)rewardNeededUser[0];
					Long count = (Long)rewardNeededUser[1];

					return managerMapper.toStampRewardNeededUserListResponse(user, count.intValue());
				}
			).toList();
	}

	@Transactional
	public void grantRewardToUser(Long userId, RewardRequest rewardRequest) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		Reward reward = managerMapper.createReward(user, rewardRequest);

		rewardRepository.save(reward);

		user.updateRewardNeeded(false);

		eventPublisher.publishEvent(managerMapper.createRewardGrantedEvent(userId, "스탬프 보상"));
	}

	public List<InquiryListItemResponse> getAllInquiries(InquirySearchCondition condition) {
		String writerTypeInput = (condition.writerType() != null) ? condition.writerType().trim() : "";

		WriterType type = Arrays.stream(WriterType.values())
			.filter(t -> t.name().equalsIgnoreCase(writerTypeInput))
			.findFirst()
			.orElse(null);

		List<Inquiry> inquiries = inquiryRepository.findAllByCondition(
			condition.status(),
			type
		);

		return inquiries.stream()
			.map(inquiryMapper::toInquiryListItemResponse)
			.toList();
	}

	@Transactional
	public void answerInquiry(Long inquiryId, InquiryAnswerRequest request) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(InquiryNotFoundException::new);

		inquiry.updateAnswer(request.answer());

		try {
			if (inquiry.getWriter() != null) {
				notificationService.saveInquiryAnsweredNotification(inquiry.getWriter(), inquiry.getId());
			} else if (inquiry.getStudentCouncilWriter() != null) {
				councilNotificationService.saveInquiryAnsweredNotification(inquiry.getStudentCouncilWriter(),
					inquiry.getId());
			} else {
				log.warn("문의 ID={}에 대한 알림 수신자(User/Council)를 찾을 수 없습니다.", inquiry.getId());
			}
		} catch (Exception e) {
			log.error("문의 답변 알림 발송 중 오류 발생: inquiryId={}", inquiry.getId());
		}
	}

	private void sendCouncilApprovedMail(String to) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("[Campus] 학생회 계정 생성 요청 승인 결과 안내");
		message.setText(
			"""
				[Campus] 학생회 계정 생성 요청 승인 결과 안내드립니다.
				
				축하합니다!!
				관리자 확인 결과 해당 학생회 계정 인증에 성공하였습니다.
				이제부터 학생회 대표자로서 Campus 서비스 이용이 가능합니다.
				
				Campus에 오신 것을 환영합니다~~~
				"""
		);

		javaMailSender.send(message);
	}

	private void sendCouncilDeniedMail(String to) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("[Campus] 학생회 계정 생성 요청 승인 결과 안내");
		message.setText(
			"""
				[Campus] 학생회 계정 생성 요청 승인 결과 안내드립니다.
				
				죄송합니다!!
				관리자 확인 결과 해당 학생회 계정 인증이 불가능하다고 판단되어 승인하지 못하였습니다.
				자세한 내용 혹은 재인증과 관련한 사항은 관리자에게 문의 바랍니다.
				"""
		);

		javaMailSender.send(message);
	}
}
