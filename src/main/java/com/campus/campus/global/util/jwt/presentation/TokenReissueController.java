package com.campus.campus.global.util.jwt.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.global.util.jwt.application.dto.request.TokenReissueRequest;
import com.campus.campus.global.util.jwt.application.dto.response.TokenReissueResponse;
import com.campus.campus.global.util.jwt.application.service.TokenReissueService;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token/reissue")
public class TokenReissueController {
	private final TokenReissueService tokenReissueService;

	@PostMapping
	@Operation(summary = "토큰 재발급")
	public CommonResponse<TokenReissueResponse> reissueToken(
		@Valid @RequestBody TokenReissueRequest tokenReissueRequest) {
		TokenReissueResponse tokenReissueResponse = tokenReissueService.reissue(tokenReissueRequest);

		return CommonResponse.success(JwtResponseCode.TOKEN_REISSUE_SUCCESS, tokenReissueResponse);
	}
}
