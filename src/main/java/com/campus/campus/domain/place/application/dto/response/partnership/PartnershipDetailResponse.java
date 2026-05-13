package com.campus.campus.domain.place.application.dto.response.partnership;

import java.time.LocalDate;
import java.util.List;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.place.application.dto.response.PlaceDetailView;
import com.campus.campus.domain.review.application.dto.response.SimpleReviewResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record PartnershipDetailResponse(
	@Schema(description = "제휴 여부", example = "true")
	boolean isPartnership,

	@Schema(description = "장소 ID", example = "1")
	Long placeId,

	@Schema(description = "장소 고유 키", example = "ChIJN1t_tDeuEmsRUsoyG83frY4")
	String placeKey,

	@Schema(description = "장소 이름", example = "스타벅스 중앙대점")
	String name,

	@Schema(description = "카테고리", example = "CAFE")
	String category,

	@Schema(description = "주소", example = "서울특별시 동작구 흑석로 84")
	String address,

	@Schema(description = "위도", example = "37.5050881")
	Double latitude,

	@Schema(description = "경도", example = "126.9571012")
	Double longitude,

	@Schema(description = "제휴 주체 (예: 총학생회, 단과대)", example = "SCHOOL_COUNCIL")
	CouncilType councilType,

	@Schema(description = "찜 여부", example = "true")
	boolean isLiked,

	@Schema(description = "리뷰 평점", example = "4.5")
	double star,

	@Schema(description = "제휴 혜택 제목", example = "전 메뉴 10% 할인")
	String partnerTitle,

	@Schema(description = "현재 위치로부터의 거리(m)", example = "150.5")
	double distance,

	@Schema(description = "제휴 종료일", example = "2024-12-31")
	LocalDate endDate,

	//StudentCouncilPost 이미지 받아오기
	@Schema(description = "이미지 URL 목록")
	List<String> imgUrls,

	@Schema(description = "최근 리뷰 목록")
	List<SimpleReviewResponse> reviews,

	@Schema(description = "총 리뷰 개수", example = "15")
	int reviewSize
) implements PlaceDetailView {
}
