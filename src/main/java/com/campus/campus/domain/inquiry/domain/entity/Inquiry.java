package com.campus.campus.domain.inquiry.domain.entity;

import java.time.LocalDateTime;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.user.domain.entity.User;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inquiry")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User writer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_council_id")
	private StudentCouncil studentCouncilWriter;

	@Enumerated(EnumType.STRING)
	private WriterType writerType;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private InquiryStatus status = InquiryStatus.WAITING;

	@Column(columnDefinition = "TEXT")
	private String answer;

	private LocalDateTime answeredAt;

	public Long getWriterId() {
		if (this.writer != null) {
			return this.writer.getId();
		}
		if (this.studentCouncilWriter != null) {
			return this.studentCouncilWriter.getId();
		}
		return null;
	}

	public String getWriterType() {
		return (this.writer != null) ? this.writerType.name() : null;
	}

	public void updateAnswer(String answer) {
		this.answer = answer;
		this.status = InquiryStatus.COMPLETED;
		this.answeredAt = LocalDateTime.now();
	}
}
