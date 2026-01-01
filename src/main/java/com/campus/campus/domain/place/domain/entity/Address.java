package com.campus.campus.domain.place.domain.entity;

import com.campus.campus.domain.place.application.exception.InvalidAddressException;

public record Address(String value) {

	public Address {
		if (value == null) {
			throw new InvalidAddressException();
		}
	}
}
