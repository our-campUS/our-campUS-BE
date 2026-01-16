package com.campus.campus.domain.notification.application.dto;

import java.util.List;

public record CursorResponse<T>(
	List<T> items,
	NextCursor nextCursor,
	boolean hasNext
) {
}
