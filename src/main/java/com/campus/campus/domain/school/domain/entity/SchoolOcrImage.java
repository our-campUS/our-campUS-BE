package com.campus.campus.domain.school.domain.entity;

import com.campus.campus.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "school_ocr_image")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)

public class SchoolOcrImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_ocr_image_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 300)
    private String objectName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OcrImageType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OcrStatus status;

    private LocalDateTime ocrCompletedAt;

    public void markOcrSuccess() {
        this.status = OcrStatus.OCR_SUCCESS;
        this.ocrCompletedAt = LocalDateTime.now();
    }

    public void markOcrFailed() {
        this.status = OcrStatus.OCR_FAILED;
        this.ocrCompletedAt = LocalDateTime.now();
    }
}
