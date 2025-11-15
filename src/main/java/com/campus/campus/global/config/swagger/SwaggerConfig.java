package com.campus.campus.global.config.swagger;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	@Value("${server-uri}")
	String serverUri;

	private static final String JWT_SCHEME = "jwtAuth";

	@Bean
	public OpenAPI openAPI() {

		Server server = new Server();
		server.setUrl(serverUri);

		return new OpenAPI()
			.addServersItem(new Server().url("/"))
			.addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME))
			.components(new Components()
				.addSecuritySchemes(JWT_SCHEME,
					new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
						.in(SecurityScheme.In.HEADER)
						.name("Authorization")
				)
			)
			.servers(List.of(server))
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("Campus")
			.description("Campus API 문서")
			.version("1.1.0");
	}
}
