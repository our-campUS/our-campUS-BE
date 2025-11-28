package com.campus.campus.domain.school.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.school.application.dto.request.SchoolFindRequest;
import com.campus.campus.domain.school.application.dto.response.SchoolFindResponse;
import com.campus.campus.domain.school.application.service.SchoolService;
import com.campus.campus.global.common.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schools")
public class SchoolController {
	private final SchoolService schoolService;

	@GetMapping("/search")
	public CommonResponse<List<SchoolFindResponse>> searchSchools(
		@Valid @RequestBody SchoolFindRequest schoolFindRequest
	) {
		List<SchoolFindResponse> schoolFindResponses = schoolService.searchSchools(schoolFindRequest);

		return CommonResponse.success(SchoolResponseCode.SCHOOL_FIND_SUCCESS, schoolFindResponses);
	}
}
