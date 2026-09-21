package com.campus.campus.global.auth.application.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.campus.campus.global.auth.exception.AppleTokenEncryptionException;
import com.campus.campus.global.auth.exception.AppleTokenExchangeException;

@Component
public class AppleTokenEncryptor {

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";

	private static final String VERSION = "v1";
	private static final String DELIMITER = ":";
	private static final String SPLIT_DELIMITER = "\\.";

	private static final int AES_256_KEY_LENGTH = 32;
	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH_BIT = 128;

	private static final byte[] AAD = "APPLE_REFRESH_TOKEN_V1".getBytes(StandardCharsets.UTF_8);

	private final SecretKey secretKey;
	private final SecureRandom secureRandom;

	public AppleTokenEncryptor(
		@Value("${security.apple-token.encryption-key}")
		String encodedKey
	) {
		this.secretKey = createSecretKey(encodedKey);
		this.secureRandom = new SecureRandom();
	}

	public String encrypt(String plainToken) {
		if (!StringUtils.hasText(plainToken)) {
			throw new IllegalArgumentException("암호화할 Apple Refresh Token이 없습니다.");
		}

		try {
			byte[] iv = createRandomIv();

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
			cipher.updateAAD(AAD);

			byte[] encrypted = cipher.doFinal(plainToken.getBytes(StandardCharsets.UTF_8));

			return String.join(DELIMITER, VERSION, encode(iv), encode(encrypted));
		} catch (GeneralSecurityException exception) {
			throw new AppleTokenEncryptionException(exception);
		}
	}

	public String decrypt(String encryptedToken) {
		if (!StringUtils.hasText(encryptedToken)) {
			throw new IllegalArgumentException("복호화할 Apple Refresh Token이 없습니다.");
		}

		try {
			EncryptedToken parsedToken = parse(encryptedToken);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, parsedToken.iv()));
			cipher.updateAAD(AAD);

			byte[] decrypted = cipher.doFinal(parsedToken.cipherText());

			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (GeneralSecurityException | IllegalArgumentException exception) {
			throw new AppleTokenEncryptionException(exception);
		}
	}

	private SecretKey createSecretKey(String encodedKey) {
		if (!StringUtils.hasText(encodedKey)) {
			throw new AppleTokenExchangeException();
		}

		final byte[] keyBytes;

		try {
			keyBytes = Base64.getDecoder().decode(encodedKey.trim());
		} catch (IllegalArgumentException exception) {
			throw new IllegalStateException("Apple Token 암호화 키는 Base64 형식이어야 합니다.", exception);
		}

		if (keyBytes.length != AES_256_KEY_LENGTH) {
			throw new IllegalStateException("Apple Token 암호화 키는 32바이트여야 합니다.");
		}

		return new SecretKeySpec(keyBytes, ALGORITHM);
	}

	private byte[] createRandomIv() {
		byte[] iv = new byte[IV_LENGTH];
		secureRandom.nextBytes(iv);

		return iv;
	}

	private EncryptedToken parse(String encryptedToken) {
		String[] parts = encryptedToken.split(SPLIT_DELIMITER, 3);

		if (parts.length != 3) {
			throw new IllegalArgumentException("잘못된 암호문 형식입니다.");
		}

		if (!VERSION.equals(parts[0])) {
			throw new IllegalArgumentException("지원하지 않는 암호문 버전입니다.");
		}

		byte[] iv = decode(parts[1]);
		byte[] cipherText = decode(parts[2]);

		if (iv.length != IV_LENGTH) {
			throw new IllegalStateException("잘못된 IV 길이입니다.");
		}

		return new EncryptedToken(iv, cipherText);
	}

	private String encode(byte[] value) {
		return Base64.getUrlEncoder()
			.withoutPadding()
			.encodeToString(value);
	}

	private byte[] decode(String value) {
		return Base64.getUrlDecoder().decode(value);
	}

	private record EncryptedToken(
		byte[] iv,
		byte[] cipherText
	) {
	}
}
