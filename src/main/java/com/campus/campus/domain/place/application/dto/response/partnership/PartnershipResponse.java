package com.campus.campus.domain.place.application.dto.response.partnership;

import java.util.List;

public record PartnershipResponse(
	Long placeId,
	String placeKey,
	String name,
	String category,
	String address,
	Double latitude,
	Double longitude,
	String tag, //(ex.) 총학생회, 사회과학대학, IT공학과
	boolean isLiked,

	//StudentCouncilPost 이미지 받아오기
	List<String> imgUrls
) {
}
