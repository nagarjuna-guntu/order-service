package com.polarbookshop.order_service.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import dasniko.testcontainers.keycloak.KeycloakContainer;

public interface MyTestContainers {
	
	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.1");
	
	@Container
	static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:26.0")
								.withRealmImportFile(("test-realm-config.json"));
	
	@DynamicPropertySource
	static void keycloakProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", 
				() -> keycloakContainer.getAuthServerUrl() + "realms/PolarBookshop");
	}

}
