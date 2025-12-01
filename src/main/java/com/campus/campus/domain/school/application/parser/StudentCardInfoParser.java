package com.campus.campus.domain.school.application.parser;

import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.dto.response.StudentCardInfoDto;
import java.util.List;

public class StudentCardInfoParser {

    public static StudentCardInfoDto parse(ClovaOcrResponseDto response) {

        List<String> texts = extractAllTexts(response);

        return new StudentCardInfoDto(
                extractName(texts),
                extractGrade(texts),
                extractStudentId(texts),
                extractDepartment(texts),
                texts
        );
    }

    private static List<String> extractAllTexts(ClovaOcrResponseDto response) {
        return response.images().get(0).fields()
                .stream()
                .map(ClovaOcrResponseDto.Field::inferText)
                .toList();
    }

    private static String extractName(List<String> texts) {
        return texts.stream()
                .filter(t -> t.matches("^[가-힣]{2,4}$"))
                .findFirst()
                .orElse(null);
    }

    private static String extractStudentId(List<String> texts) {
        return texts.stream()
                .filter(t -> t.matches("^\\d{10}$"))
                .findFirst()
                .orElse(null);
    }

    private static String extractDepartment(List<String> texts) {
        return texts.stream()
                .filter(t -> t.contains("학과") || t.contains("학부"))
                .findFirst()
                .orElse(null);
    }

    private static String extractGrade(List<String> texts) {
        return texts.stream()
                .filter(t -> t.matches("^[1-4]학년$"))
                .map(t -> t.replace("학년", ""))
                .findFirst()
                .orElse(null);
    }
}