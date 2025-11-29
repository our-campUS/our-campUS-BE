package com.campus.campus.domain.school.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.school.application.dto.response.SchoolFindResponse;
import com.campus.campus.domain.school.application.service.SchoolService;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SchoolController {
	private final SchoolService schoolService;

	@GetMapping("/schools")
	@Operation(summary = "학교 검색")
	public CommonResponse<List<SchoolFindResponse>> searchSchools(
		@Valid @RequestParam String keyword
	) {
		List<SchoolFindResponse> schoolFindResponses = schoolService.searchSchools(keyword);

		return CommonResponse.success(SchoolResponseCode.SCHOOL_FIND_SUCCESS, schoolFindResponses);
	}
}
