package com.campus.campus.domain.school.application.service.ocr;

import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.dto.response.StudentCardInfoDto;
import com.campus.campus.domain.school.application.dto.response.StudentCouncilInfoDto;
import com.campus.campus.domain.school.application.parser.StudentCardInfoParser;
import com.campus.campus.domain.school.application.parser.StudentCouncilInfoParser;
import com.campus.campus.global.ocr.service.OciObjectDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClovaOcrService {

    private final OciObjectDownloadService downloadService;
    private final ClovaOcrCallerService callerService;

    public StudentCardInfoDto processStudentCard(String objectName) {
        byte[] fileBytes = downloadService.download(objectName);
        ClovaOcrResponseDto ocr = callerService.callOcr(fileBytes, objectName);

        return StudentCardInfoParser.parse(ocr);
    }

    public StudentCouncilInfoDto processStudentCouncil(String objectName) {
        byte[] fileBytes = downloadService.download(objectName);
        ClovaOcrResponseDto ocr = callerService.callOcr(fileBytes, objectName);

        return StudentCouncilInfoParser.parse(ocr);
    }
}
