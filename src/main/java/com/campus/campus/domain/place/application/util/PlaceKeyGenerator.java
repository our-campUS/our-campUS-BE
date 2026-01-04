package com.campus.campus.domain.place.application.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.place.application.exception.Sha256AlgorithmException;

@Component
public class PlaceKeyGenerator {

	public static String generate(String rawName, String rawAddress) {

		String name = normalize(rawName);
		String address = normalize(rawAddress);

		String source = name + "|" + address;
		return sha256(source);
	}

	private static String normalize(String value) {
		if (value == null) {
			return "";
		}

		return value
			.replaceAll("<[^>]*>", "")
			.toLowerCase()
			.replaceAll("\\s+", "")
			.replaceAll("[^가-힣a-z0-9]", ""); //특수문자 제거
	}

	private static String sha256(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

			return HexFormat.of().formatHex(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new Sha256AlgorithmException();
		}
	}

}
