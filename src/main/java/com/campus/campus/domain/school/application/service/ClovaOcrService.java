package com.campus.campus.domain.school.application.service;

import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.dto.response.StudentCardInfoDto;
import com.campus.campus.domain.school.application.dto.response.StudentCouncilInfoDto;
import com.campus.campus.domain.school.application.service.ocr.ClovaOcrCallerService;
import com.campus.campus.domain.school.application.service.ocr.StudentCardParserService;
import com.campus.campus.domain.school.application.service.ocr.StudentCouncilParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ClovaOcrService {

    private final ClovaOcrCallerService callerService;
    private final StudentCardParserService cardParserService;
    private final StudentCouncilParserService councilParserService;

    /** ① 학생증 OCR */
    public StudentCardInfoDto processStudentCard(MultipartFile file) {
        ClovaOcrResponseDto ocr = callerService.callOcr(file, "student-card");
        return cardParserService.parse(ocr);
    }

    /** ② 당선 정보 OCR */
    public StudentCouncilInfoDto processStudentCouncil(MultipartFile file) {
        ClovaOcrResponseDto ocr = callerService.callOcr(file, "student-council");
        return councilParserService.parse(ocr);
    }
}