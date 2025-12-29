package com.campus.campus.domain.studentCouncilNotice.domain.entity;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudentCouncilNotice extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "writer_id")
	private StudentCouncil writer;

	@Column(nullable = false, length = 100)
	private String title;

	@Lob
	@Column(nullable = false)
	private String content;

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public boolean isWrittenByCouncil(Long councilId) {
		return writer != null && writer.getId().equals(councilId);
	}
}
