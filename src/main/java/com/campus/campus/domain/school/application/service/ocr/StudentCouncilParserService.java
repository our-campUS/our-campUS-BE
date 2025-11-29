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
public class StudentCouncilParserService {

    public StudentCouncilInfoDto parse(ClovaOcrResponseDto ocr) {

        List<String> texts = flattenTexts(ocr);
        String allText = String.join(" ", texts);

        String unit      = findUnit(allText, texts);
        String studentId = findStudentId(texts);
        String grade     = findGrade(texts);
        String dept      = findDepartment(texts);
        String position  = classifyPosition(allText);
        String winner    = findWinnerName(allText, texts);

        return new StudentCouncilInfoDto(unit, winner, studentId, grade, dept, position, allText);
    }

    // ---------- 공통 전처리 ----------
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

    // ---------- 이름/학번/학년/학과 (학생증과 동일) ----------
    private String findStudentId(List<String> texts) {
        return texts.stream()
                .filter(t -> t.matches("^\\d{8,10}$"))
                .findFirst()
                .orElse(null);
    }

    private String findGrade(List<String> texts) {
        return texts.stream()
                .filter(t -> t.matches("^[1-8]$"))
                .findFirst()
                .orElse(null);
    }

    private String findDepartment(List<String> texts) {
        return texts.stream()
                .filter(t -> t.contains("학과") || t.contains("학부"))
                .findFirst()
                .orElse(null);
    }

    // ---------- 당선 정보 전용 ----------

    private String findUnit(String all, List<String> texts) {
        List<String> candidates = List.of(
                "총학생회", "단과대학생회", "단과대 학생회",
                "학부학생회", "학과학생회", "과학생회"
        );
        for (String c : candidates)
            if (all.contains(c)) return c;

        if (all.contains("학생회")) return "학생회";
        return null;
    }

    private String classifyPosition(String all) {
        if (all == null) return null;

        Map<String, List<String>> dict = new LinkedHashMap<>();
        dict.put("총학생회장", List.of("총학생회장", "총학회장"));
        dict.put("부총학생회장", List.of("부총학생회장", "부학생회장", "부회장"));
        dict.put("학생회장", List.of("학생회장", "학회장", "회장"));

        for (var entry : dict.entrySet()) {
            for (String keyword : entry.getValue())
                if (all.contains(keyword))
                    return entry.getKey();
        }
        return null;
    }

    private String findWinnerName(String all, List<String> texts) {
        Pattern p1 = Pattern.compile("당선인\\s*([가-힣]{2,4})");
        Matcher m1 = p1.matcher(all);
        if (m1.find()) return m1.group(1);

        return texts.stream()
                .filter(t -> t.matches("^[가-힣]{2,4}$"))
                .findFirst()
                .orElse(null);
    }
}
