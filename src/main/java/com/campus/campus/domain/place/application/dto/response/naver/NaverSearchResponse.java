package com.campus.campus.domain.place.application.dto.response.naver;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record NaverSearchResponse(
	@Schema(description = "검색 결과 아이템 목록")
	List<Item> items
) {

	public record Item(
		@Schema(description = "장소명 (HTML 태그 포함될 수 있음)", example = "<b>중앙대학교</b> 서울캠퍼스")
		String title,

		@Schema(description = "카테고리", example = "대학교")
		String category,

		@Schema(description = "지번 주소", example = "서울특별시 동작구 흑석동 221")
		String address,

		@Schema(description = "도로명 주소", example = "서울특별시 동작구 흑석로 84")
		String roadAddress,

		@Schema(description = "상세 정보 링크", example = "http://www.cau.ac.kr/")
		String link,

		@Schema(description = "전화번호", example = "02-820-5114")
		String telephone,

		@Schema(description = "X 좌표", example = "307340")
		String mapx,

		@Schema(description = "Y 좌표", example = "549505")
		String mapy
	) {
	}
}
