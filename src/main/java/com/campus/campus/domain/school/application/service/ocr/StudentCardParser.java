package com.campus.campus.domain.school.application.service.ocr;

import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.dto.response.StudentCardInfoDto;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StudentCardParser {

    private static final Pattern NAME_REGEX = Pattern.compile("^[가-힣]{2,4}$");
    private static final Pattern STUDENT_ID_REGEX = Pattern.compile("^\\d{8,10}$");
    private static final Pattern GRADE_KOREAN_REGEX = Pattern.compile("^[1-8]학년$");
    private static final Pattern GRADE_NUMERIC_REGEX = Pattern.compile("^[1-8]$");

    public StudentCardInfoDto parse(ClovaOcrResponseDto ocr) {

        List<String> texts = flattenTexts(ocr);

        String name      = findKoreanName(texts);
        String studentId = findStudentId(texts);
        String grade     = findGrade(texts);
        String dept      = findDepartment(texts);

        return new StudentCardInfoDto(name, grade, studentId, dept, texts);
    }

    // ------------------------------------------
    // 1) 필드 텍스트 모두 평탄화
    // ------------------------------------------
    private List<String> flattenTexts(ClovaOcrResponseDto ocr) {
        if (ocr == null || ocr.images() == null)
            return List.of();

        return ocr.images().stream()
                .filter(img -> img.fields() != null)
                .flatMap(img -> img.fields().stream())
                .map(ClovaOcrResponseDto.Field::inferText)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    // ------------------------------------------
    // 2) 이름 (순수 한글 2~4자)
    // ------------------------------------------
    private String findKoreanName(List<String> texts) {
        return texts.stream()
                .filter(t -> NAME_REGEX.matcher(t).matches())
                .findFirst()
                .orElse(null);
    }

    // ------------------------------------------
    // 3) 학번 (숫자 8~10자리)
    // ------------------------------------------
    private String findStudentId(List<String> texts) {
        return texts.stream()
                .filter(t -> STUDENT_ID_REGEX.matcher(t).matches())
                .findFirst()
                .orElse(null);
    }

    // ------------------------------------------
    // 4) 학년 ("4학년" → 4) 또는 숫자 directly
    // ------------------------------------------
    private String findGrade(List<String> texts) {

        // Case 1: "4학년"
        Optional<String> direct = texts.stream()
                .filter(t -> GRADE_KOREAN_REGEX.matcher(t).matches())
                .findFirst();

        if (direct.isPresent()) {
            return direct.get().substring(0, 1);
        }

        // Case 2: "4"
        return texts.stream()
                .filter(t -> GRADE_NUMERIC_REGEX.matcher(t).matches())
                .findFirst()
                .orElse(null);
    }

    // ------------------------------------------
    // 5) 학과명 (학부/학과/전공 포함)
    // ------------------------------------------
    private String findDepartment(List<String> texts) {

        return texts.stream()
                .filter(t ->
                        t.contains("학과") ||
                                t.contains("학부") ||
                                t.contains("전공")
                )
                .findFirst()
                .orElse(null);
    }
}
