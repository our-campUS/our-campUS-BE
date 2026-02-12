package com.campus.campus.domain.place.application.dto.response.geocoder;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressResponse {

	@Schema(description = "응답 본문")
	private Response response;

	@Getter
	public static class Response {
		@Schema(description = "응답 상태", example = "OK")
		private String status;

		@Schema(description = "주소 검색 결과 목록")
		private List<Result> result;
	}

	@Getter
	public static class Result {
		@Schema(description = "검색 결과 타입", example = "PARCEL")
		private String type;

		@Schema(description = "전체 주소 텍스트", example = "경기도 성남시 분당구 불정로 6")
		private String text;

		@Schema(description = "주소 구조 정보")
		private Structure structure;
	}

	@Getter
	public static class Structure {
		@Schema(description = "광역자치단체(도/시)", example = "경기도")
		private String level1;

		@Schema(description = "기초자치단체(시/군/구)", example = "성남시 분당구")
		private String level2;

		@Schema(description = "읍/면/동", example = "정자동")
		private String level3;

		@Schema(description = "도로명/리", example = "불정로")
		private String level4L;

		@Schema(description = "행정동/상세", example = "6")
		private String level4A;
	}
}
