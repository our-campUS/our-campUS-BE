package com.campus.campus.domain.councilpost.domain.entity;

import java.time.LocalTime;

import com.campus.campus.domain.councilpost.application.dto.response.NormalizedDateTime;
import com.campus.campus.domain.councilpost.application.dto.request.PostRequest;
import com.campus.campus.domain.councilpost.application.exception.EventEndDateTimeNotAllowedException;
import com.campus.campus.domain.councilpost.application.exception.EventStartDateTimeRequiredException;
import com.campus.campus.domain.councilpost.application.exception.PartnershipDateRequiredException;

public enum PostCategory {

	EVENT {
		@Override
		public NormalizedDateTime validateAndNormalize(PostRequest dto) {
			if (dto.startDateTime() == null) {
				throw new EventStartDateTimeRequiredException();
			}
			if (dto.endDateTime() != null) {
				throw new EventEndDateTimeNotAllowedException();
			}
			return new NormalizedDateTime(dto.startDateTime(), null);
		}
	},

	PARTNERSHIP {
		@Override
		public NormalizedDateTime validateAndNormalize(PostRequest dto) {
			if (dto.startDateTime() == null || dto.endDateTime() == null) {
				throw new PartnershipDateRequiredException();
			}
			return new NormalizedDateTime(
				dto.startDateTime().with(LocalTime.MIN),
				dto.endDateTime().with(LocalTime.MAX)
			);
		}
	};

	public abstract NormalizedDateTime validateAndNormalize(PostRequest dto);
}
