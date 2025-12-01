package com.campus.campus.domain.school.application.service;

import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.dto.response.StudentCardInfoDto;
import com.campus.campus.domain.school.application.dto.response.StudentCouncilInfoDto;
import com.campus.campus.domain.school.application.service.ocr.ClovaOcrCallerService;
import com.campus.campus.domain.school.application.service.ocr.StudentCardParser;
import com.campus.campus.domain.school.application.service.ocr.StudentCouncilParser;
import com.campus.campus.domain.school.domain.entity.SchoolOcrImage;
import com.campus.campus.domain.school.domain.repository.SchoolOcrImageRepository;
import com.campus.campus.global.ocr.service.OciObjectDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolOcrService {

    private final ClovaOcrCallerService ocrCaller;
    private final StudentCardParser studentCardParser;
    private final StudentCouncilParser studentCouncilParser;
    private final SchoolOcrImageRepository imageRepository;
    private final OciObjectDownloadService ociObjectDownloadService;

    // --------------------------
    // 1) 학생증 OCR
    // --------------------------
    public StudentCardInfoDto processStudentCard(Long userId, Long imageId) {

        SchoolOcrImage image = imageRepository.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new IllegalArgumentException("이미지 없음"));

        byte[] fileBytes = ociObjectDownloadService.download(image.getObjectName());

        ClovaOcrResponseDto ocr = ocrCaller.callOcr(fileBytes, image.getObjectName());

        return studentCardParser.parse(ocr);
    }


    // --------------------------
    // 2) 학생회 당선증 OCR
    // --------------------------
    public StudentCouncilInfoDto processStudentCouncil(Long userId, Long imageId) {

        SchoolOcrImage image = imageRepository.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new IllegalArgumentException("이미지 없음"));

        byte[] fileBytes = ociObjectDownloadService.download(image.getObjectName());

        ClovaOcrResponseDto ocr = ocrCaller.callOcr(fileBytes, image.getObjectName());

        return studentCouncilParser.parse(ocr);
    }
}

