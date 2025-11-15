package com.campus.campus.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.global.common.response.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
	@GetMapping
	public CommonResponse<Void> test() {
		System.out.println("test");
		return CommonResponse.success(TestResponseCode.TEST_SUCCESS);
	}
}
