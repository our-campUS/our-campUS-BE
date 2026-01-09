package com.campus.campus.domain.place.application.dto.response.geocoder;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressResponse {

	private Response response;

	@Getter
	public static class Response {
		private String status;
		private List<Result> result;
	}

	@Getter
	public static class Result {
		private String type;
		private String text;
		private Structure structure;
	}

	@Getter
	public static class Structure {
		private String level1;
		private String level2;
		private String level3;
		private String level4L;
		private String level4A;
	}
}
