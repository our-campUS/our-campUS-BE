package com.campus.campus.domain.place.application.dto.response.partnership;

import java.time.LocalDate;
import java.util.List;

import com.campus.campus.domain.place.application.dto.response.PlaceDetailView;
import com.campus.campus.domain.review.application.dto.response.SimpleReviewResponse;

public record PartnershipDetailResponse(
	Long placeId,
	String placeKey,
	String name,
	String category,
	String address,
	Double latitude,
	Double longitude,
	String tag, //(ex.) 총학생회, 사회과학대학, IT공학과
	boolean isLiked,
	double star, //리뷰 평점
	String partnerTitle, //제휴 제목
	double distance, //거리(m)
	LocalDate endDate, //제휴 끝나는 시점

	//StudentCouncilPost 이미지 받아오기
	List<String> imgUrls,
	List<SimpleReviewResponse> reviews,
	int reviewSize
) implements PlaceDetailView {
}
