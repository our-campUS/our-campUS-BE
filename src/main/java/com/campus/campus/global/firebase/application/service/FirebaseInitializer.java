package com.campus.campus.global.firebase.application.service;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.campus.campus.global.firebase.exception.FirebaseInitializationFailedException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Component
@ConditionalOnProperty(prefix="firebase", name="enabled", havingValue="true", matchIfMissing=true)
public class FirebaseInitializer {

	@PostConstruct
	public void initialize() {
		if (!FirebaseApp.getApps().isEmpty()) {
			return;
		}

		try (InputStream serviceAccount = new ClassPathResource("keys/campus-firebase.json").getInputStream()) {

			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();
			FirebaseApp.initializeApp(options);

		} catch (IOException e) {
			throw new FirebaseInitializationFailedException();
		}
	}
}
