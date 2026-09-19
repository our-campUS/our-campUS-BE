package com.campus.campus.domain.review.application.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.campus.campus.domain.review.application.exception.InappropriateReviewContentException;
import com.campus.campus.domain.review.domain.ReviewModerationResult;
import com.campus.campus.domain.review.domain.entity.ProhibitedWordSeverity;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProhibitedWordValidator {

	private static final String CSV_FILE_PATH = "prohibited_words.csv";
	private static final Pattern PHONE_PATTERN = Pattern.compile("01[0-9][-. ]?\\d{3,4}[-. ]?\\d{4}");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
	private static final Pattern ACCOUNT_PATTERN = Pattern.compile("\\d{2,6}-\\d{2,6}-\\d{2,10}");

	private final Map<String, ProhibitedWordSeverity> prohibitedWords = new HashMap<>();

	@PostConstruct
	public void loadProhibitedWords() {
		ClassPathResource resource = new ClassPathResource(CSV_FILE_PATH);

		try (
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
			)
		) {
			reader.lines()
				.skip(1)
				.map(String::trim)
				.filter(line -> !line.isBlank())
				.forEach(this::parseLine);

			log.info("금칙어 CSV 로딩 완료: {}개", prohibitedWords.size());

		} catch (IOException e) {
			log.error("금칙어 CSV 로딩 실패", e);
			throw new IllegalStateException("금칙어 CSV 파일을 불러오지 못했습니다.", e);
		}
	}

	//CSV 한 줄(word,severity)을 파싱해서 맵에 등록
	private void parseLine(String line) {
		String[] tokens = line.split(",", 2);
		if (tokens.length != 2) {
			log.warn("금칙어 CSV 형식 오류, 스킵합니다: {}", line);
			return;
		}

		String word = tokens[0].trim().toLowerCase();
		String severityRaw = tokens[1].trim().toUpperCase();

		if (word.isBlank()) {
			return;
		}

		try {
			ProhibitedWordSeverity severity = ProhibitedWordSeverity.valueOf(severityRaw);
			prohibitedWords.put(word, severity);
		} catch (IllegalArgumentException e) {
			log.warn("알 수 없는 금칙어 등급, 스킵합니다: {} ({})", word, severityRaw);
		}
	}

	//리뷰 내용을 검사해 처리 방식을 반환
	//BLOCK: 등록 차단 / NEEDS_REVIEW: 등록되지만 비공개 검토 상태 / PASS: 정상
	public ReviewModerationResult validate(String content) {
		if (content == null || content.isBlank()) {
			return ReviewModerationResult.PASS;
		}

		String normalizedContent = normalize(content);

		if (containsPersonalInfo(content) || containsSeverity(normalizedContent, ProhibitedWordSeverity.BLOCK)) {
			return ReviewModerationResult.BLOCK;
		}

		if (containsSeverity(normalizedContent, ProhibitedWordSeverity.REVIEW)) {
			return ReviewModerationResult.NEEDS_REVIEW;
		}

		return ReviewModerationResult.PASS;
	}

	private boolean containsSeverity(String normalizedContent, ProhibitedWordSeverity severity) {
		return prohibitedWords.entrySet().stream()
			.filter(entry -> entry.getValue() == severity)
			.map(Map.Entry::getKey)
			.anyMatch(normalizedContent::contains);
	}

	//전화번호/이메일/계좌번호 패턴 탐지
	private boolean containsPersonalInfo(String content) {
		return PHONE_PATTERN.matcher(content).find()
			|| EMAIL_PATTERN.matcher(content).find()
			|| ACCOUNT_PATTERN.matcher(content).find();
	}

	private String normalize(String content) {
		return content
			.toLowerCase()
			.replaceAll("\\s+", "");
	}
}