package com.campus.campus.domain.school.application.service.ocr;

import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.dto.response.StudentCouncilInfoDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StudentCouncilParser {

    public StudentCouncilInfoDto parse(ClovaOcrResponseDto ocr) {

        List<String> texts = flattenTexts(ocr);
        String allText = String.join(" ", texts);

        String name       = findWinnerName(allText, texts);
        String position   = classifyPosition(allText);
        String department = findDepartment(texts);

        return new StudentCouncilInfoDto(name, position, department, texts);
    }


    // ------------------------------------------
    // OCR 평탄화 (record 기반)
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
                .collect(Collectors.toList());
    }


    // ------------------------------------------
    // 학과 / 학부 추출
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


    // ------------------------------------------
    // 학생회 직책 추출
    // ------------------------------------------
    private String classifyPosition(String all) {

        Map<String, List<String>> dict = new LinkedHashMap<>();

        // 대표적인 직책들
        dict.put("총학생회장", List.of("총학생회장", "총학회장"));
        dict.put("부총학생회장", List.of("부총학생회장", "부학생회장", "부회장"));
        dict.put("학생회장", List.of("학생회장", "학회장", "회장"));

        for (var entry : dict.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (all.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }


    // ------------------------------------------
    // 당선자 이름 추출
    // ------------------------------------------
    private String findWinnerName(String all, List<String> texts) {

        // Case 1: "당선인 최서연"
        Pattern p1 = Pattern.compile("당선인\\s*([가-힣]{2,4})");
        Matcher m1 = p1.matcher(all);
        if (m1.find()) return m1.group(1);

        // Case 2: "(최서연)"
        Pattern p2 = Pattern.compile("\\(([가-힣]{2,4})\\)");
        Matcher m2 = p2.matcher(all);
        if (m2.find()) return m2.group(1);

        // Case 3: OCR 텍스트 내 순수 한글 이름
        return texts.stream()
                .filter(t -> t.matches("^[가-힣]{2,4}$"))
                .findFirst()
                .orElse(null);
    }
}
