package com.campus.campus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CampusApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusApplication.class, args);
	}

}
