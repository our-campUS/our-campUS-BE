package com.campus.campus;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CampusApplicationTests {
	@LocalServerPort
	int port;

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void healthCheck() {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/actuator/health",
			String.class);
		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
	}

	@Test
	void contextLoads() {
	}

}
