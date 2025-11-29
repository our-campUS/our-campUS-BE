package com.campus.campus.domain.school.application.service.ocr;

import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.dto.response.StudentCardInfoDto;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StudentCardParserService {

    public StudentCardInfoDto parse(ClovaOcrResponseDto ocr) {

        List<String> texts = flattenTexts(ocr);

        String name      = findKoreanName(texts);
        String studentId = findStudentId(texts);
        String grade     = findGrade(texts);
        String dept      = findDepartment(texts);

        return new StudentCardInfoDto(name, grade, studentId, dept, texts);
    }

    // ---------- 공통 텍스트 전처리 ----------
    private List<String> flattenTexts(ClovaOcrResponseDto ocr) {
        if (ocr == null || ocr.getImages() == null || ocr.getImages().isEmpty())
            return List.of();

        return ocr.getImages().get(0).getFields().stream()
                .map(ClovaOcrResponseDto.Field::getInferText)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    // ---------- 학생증 전용 파싱 ----------

    private String findKoreanName(List<String> texts) {
        return texts.stream()
                .filter(t -> t.matches("^[가-힣]{2,4}$"))
                .findFirst()
                .orElse(null);
    }

    private String findStudentId(List<String> texts) {
        return texts.stream()
                .filter(t -> t.matches("^\\d{8,10}$"))
                .findFirst()
                .orElse(null);
    }

    private String findGrade(List<String> texts) {
        Optional<String> direct = texts.stream()
                .filter(t -> t.matches("^[1-8]학년$"))
                .findFirst();
        if (direct.isPresent()) return direct.get().substring(0, 1);

        return texts.stream()
                .filter(t -> t.matches("^[1-8]$"))
                .findFirst()
                .orElse(null);
    }

    private String findDepartment(List<String> texts) {
        Optional<String> dept = texts.stream()
                .filter(t -> t.contains("학과") || t.contains("학부"))
                .findFirst();

        return dept.orElse(null);
    }
}
